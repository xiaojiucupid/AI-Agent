package cn.bugstack.ai.config;

import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;

@Configuration
public class AiAgentConfig {

//    @Bean
//    public VectorStore vectorStore() {
//        OpenAiApi openAiApi = OpenAiApi.builder()
//                .baseUrl("https://apis.itedus.cn")
//                .apiKey("sk-IfXD0bpmszHCQkn2A9Eb05E809F1443a9a6d564aFf133152")
//                .build();
//
//        return SimpleVectorStore.builder(new OpenAiEmbeddingModel(openAiApi)).build();
//    }

    /**
     * 为 PgVector 创建专用的数据源
     */
    @Bean("pgVectorDataSource")
    public DataSource pgVectorDataSource(@Value("${spring.ai.vectorstore.pgvector.datasource.driver-class-name}") String driverClassName,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.url}") String url,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.username}") String username,
                                         @Value("${spring.ai.vectorstore.pgvector.datasource.password}") String password) {
        return DataSourceBuilder.create()
                .driverClassName(driverClassName)
                .url(url)
                .username(username)
                .password(password)
                .build();
    }

    /**
     * 为 PgVector 创建专用的 JdbcTemplate
     */
    @Bean("pgVectorJdbcTemplate")
    public JdbcTemplate pgVectorJdbcTemplate(@Qualifier("pgVectorDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * -- 删除旧的表（如果存在）
     * DROP TABLE IF EXISTS public.vector_store_openai;
     *
     * -- 创建新的表，使用UUID作为主键
     * CREATE TABLE public.vector_store_openai (
     *     id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
     *     content TEXT NOT NULL,
     *     metadata JSONB,
     *     embedding VECTOR(1536)
     * );
     *
     * SELECT * FROM vector_store_openai
     */
    @Bean("vectorStore")
    public PgVectorStore pgVectorStore(@Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate) {
        OpenAiApi openAiApi = OpenAiApi.builder()
                .baseUrl("https://apis.itedus.cn")
                .apiKey("sk-IfXD0bpmszHCQkn2A9Eb05E809F1443a9a6d564aFf133152")
                .build();

        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel(openAiApi);
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .vectorTableName("vector_store_openai")
                .build();
    }

    @Bean
    public TokenTextSplitter tokenTextSplitter() {
        return new TokenTextSplitter();
    }

}
