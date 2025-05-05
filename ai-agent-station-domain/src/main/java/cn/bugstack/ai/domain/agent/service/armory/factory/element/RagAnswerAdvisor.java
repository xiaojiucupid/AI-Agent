package cn.bugstack.ai.domain.agent.service.armory.factory.element;

import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RagAnswerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    public static final String RETRIEVED_DOCUMENTS = "qa_retrieved_documents";
    public static final String FILTER_EXPRESSION = "qa_filter_expression";
    private static final String DEFAULT_USER_TEXT_ADVISE = "\nContext information is below, surrounded by ---------------------\n\n---------------------\n{question_answer_context}\n---------------------\n\nGiven the context and provided history information and not prior knowledge,\nreply to the user comment. If the answer is not in the context, inform\nthe user that you can't answer the question.\n";
    private static final int DEFAULT_ORDER = 0;
    private final VectorStore vectorStore;
    private final String userTextAdvise;
    private final SearchRequest searchRequest;
    private final boolean protectFromBlocking;
    private final int order;

    public RagAnswerAdvisor(VectorStore vectorStore) {
        this(vectorStore, SearchRequest.builder().build(), "\nContext information is below, surrounded by ---------------------\n\n---------------------\n{question_answer_context}\n---------------------\n\nGiven the context and provided history information and not prior knowledge,\nreply to the user comment. If the answer is not in the context, inform\nthe user that you can't answer the question.\n");
    }

    public RagAnswerAdvisor(VectorStore vectorStore, SearchRequest searchRequest) {
        this(vectorStore, searchRequest, "\nContext information is below, surrounded by ---------------------\n\n---------------------\n{question_answer_context}\n---------------------\n\nGiven the context and provided history information and not prior knowledge,\nreply to the user comment. If the answer is not in the context, inform\nthe user that you can't answer the question.\n");
    }

    public RagAnswerAdvisor(VectorStore vectorStore, SearchRequest searchRequest, String userTextAdvise) {
        this(vectorStore, searchRequest, userTextAdvise, true);
    }

    public RagAnswerAdvisor(VectorStore vectorStore, SearchRequest searchRequest, String userTextAdvise, boolean protectFromBlocking) {
        this(vectorStore, searchRequest, userTextAdvise, protectFromBlocking, 0);
    }

    public RagAnswerAdvisor(VectorStore vectorStore, SearchRequest searchRequest, String userTextAdvise, boolean protectFromBlocking, int order) {
        Assert.notNull(vectorStore, "The vectorStore must not be null!");
        Assert.notNull(searchRequest, "The searchRequest must not be null!");
        Assert.hasText(userTextAdvise, "The userTextAdvise must not be empty!");
        this.vectorStore = vectorStore;
        this.searchRequest = searchRequest;
        this.userTextAdvise = userTextAdvise;
        this.protectFromBlocking = protectFromBlocking;
        this.order = order;
    }

    public static Builder builder(VectorStore vectorStore) {
        return new Builder(vectorStore);
    }

    public String getName() {
        return this.getClass().getSimpleName();
    }

    public int getOrder() {
        return this.order;
    }

    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedRequest advisedRequest2 = this.before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest2);
        return this.after(advisedResponse);
    }

    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        Flux<AdvisedResponse> advisedResponses = this.protectFromBlocking ? Mono.just(advisedRequest).publishOn(Schedulers.boundedElastic()).map(this::before).flatMapMany((request) -> chain.nextAroundStream(request)) : chain.nextAroundStream(this.before(advisedRequest));
        return advisedResponses.map((ar) -> {
            if (this.onFinishReason().test(ar)) {
                ar = this.after(ar);
            }

            return ar;
        });
    }

    private AdvisedRequest before(AdvisedRequest request) {
        HashMap<String, Object> context = new HashMap(request.adviseContext());
        String var10000 = request.userText();
        String advisedUserText = var10000 + System.lineSeparator() + this.userTextAdvise;
        String query = (new PromptTemplate(request.userText(), request.userParams())).render();
        SearchRequest searchRequestToUse = SearchRequest.from(this.searchRequest).query(query).filterExpression(this.doGetFilterExpression(context)).build();
        List<Document> documents = this.vectorStore.similaritySearch(searchRequestToUse);
        context.put("qa_retrieved_documents", documents);
        String documentContext = (String)documents.stream().map(Document::getText).collect(Collectors.joining(System.lineSeparator()));
        Map<String, Object> advisedUserParams = new HashMap(request.userParams());
        advisedUserParams.put("question_answer_context", documentContext);
        AdvisedRequest advisedRequest = AdvisedRequest.from(request).userText(advisedUserText).userParams(advisedUserParams).adviseContext(context).build();
        return advisedRequest;
    }

    private AdvisedResponse after(AdvisedResponse advisedResponse) {
        ChatResponse.Builder chatResponseBuilder = ChatResponse.builder().from(advisedResponse.response());
        chatResponseBuilder.metadata("qa_retrieved_documents", advisedResponse.adviseContext().get("qa_retrieved_documents"));
        return new AdvisedResponse(chatResponseBuilder.build(), advisedResponse.adviseContext());
    }

    protected Filter.Expression doGetFilterExpression(Map<String, Object> context) {
        return context.containsKey("qa_filter_expression") && StringUtils.hasText(context.get("qa_filter_expression").toString()) ? (new FilterExpressionTextParser()).parse(context.get("qa_filter_expression").toString()) : this.searchRequest.getFilterExpression();
    }

    private Predicate<AdvisedResponse> onFinishReason() {
        return (advisedResponse) -> advisedResponse.response().getResults().stream().filter((result) -> result != null && result.getMetadata() != null && StringUtils.hasText(result.getMetadata().getFinishReason())).findFirst().isPresent();
    }

    public static final class Builder {
        private final VectorStore vectorStore;
        private SearchRequest searchRequest = SearchRequest.builder().build();
        private String userTextAdvise = "\nContext information is below, surrounded by ---------------------\n\n---------------------\n{question_answer_context}\n---------------------\n\nGiven the context and provided history information and not prior knowledge,\nreply to the user comment. If the answer is not in the context, inform\nthe user that you can't answer the question.\n";
        private boolean protectFromBlocking = true;
        private int order = 0;

        private Builder(VectorStore vectorStore) {
            Assert.notNull(vectorStore, "The vectorStore must not be null!");
            this.vectorStore = vectorStore;
        }

        public Builder searchRequest(SearchRequest searchRequest) {
            Assert.notNull(searchRequest, "The searchRequest must not be null!");
            this.searchRequest = searchRequest;
            return this;
        }

        public Builder userTextAdvise(String userTextAdvise) {
            Assert.hasText(userTextAdvise, "The userTextAdvise must not be empty!");
            this.userTextAdvise = userTextAdvise;
            return this;
        }

        public Builder protectFromBlocking(boolean protectFromBlocking) {
            this.protectFromBlocking = protectFromBlocking;
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public RagAnswerAdvisor build() {
            return new RagAnswerAdvisor(this.vectorStore, this.searchRequest, this.userTextAdvise, this.protectFromBlocking, this.order);
        }
    }
}

