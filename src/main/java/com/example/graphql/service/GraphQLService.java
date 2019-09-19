package com.example.graphql.service;


import com.example.graphql.dao.BookDao;
import com.example.graphql.model.Book;
import com.example.graphql.service.datafetchers.AllBooksDataFetcher;
import com.example.graphql.service.datafetchers.BookByIsnDataFetcher;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class GraphQLService {

    @Value("classpath:books.graphql")
    private Resource schemaResource;

    @Autowired
    private BookDao bookDao;

    @Getter
    private GraphQL graphQL;

    @Autowired
    private AllBooksDataFetcher allBooksDataFetcher;

    @Autowired
    private BookByIsnDataFetcher bookByIsnFetcher;

    @PostConstruct
    public void loadSchema() throws IOException {
        populateDB();
        File schemaFile = schemaResource.getFile();
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(schemaFile);
        RuntimeWiring runtimeWiring = buildRuntimeWiring();
        GraphQLSchema schema = new SchemaGenerator().makeExecutableSchema(typeRegistry, runtimeWiring);
        graphQL = GraphQL.newGraphQL(schema).build();
    }

    private void populateDB() {
        bookDao.saveAll(Arrays.asList(
                new Book("1234", "Godfather", 350, 5),
                new Book("2345", "Godfather II", 300, 4),
                new Book("3456", "Godfather III", 500, 3)
        ));
    }

    private RuntimeWiring buildRuntimeWiring() {
        return RuntimeWiring.newRuntimeWiring()
                .type("Query", typeWiring -> typeWiring
                        .dataFetcher("allBooks", allBooksDataFetcher)
                        .dataFetcher("getBookByIsn", bookByIsnFetcher))
                .build();
    }


}
