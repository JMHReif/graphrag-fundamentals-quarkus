# graphrag-fundamentals-quarkus

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

Test the endpoints.

```shell
#Graph query
http ":8080/articleMentions"

#LLM questions
http ":8080/llm?question=Who is Jennifer Reif?"
http ":8080/llm?question=Could you create a short poem about technology in the style of Emily Dickinson?"
http ":8080//llm?question=Who is Jennifer Reif using this as context? Jennifer Reif is a developer advocate at Neo4j, focusing on the Java ecosystem. She is a technical speaker, blogger, podcaster, and author, with an MS in CMIS."
http ":8080/llm?question=What is the latest news about toys?"

#Vector RAG questions
http ":8080/vectorRAG?question=What is the latest news about toys?"
##bare minimum results
http ":8080/vectorRAG?question=What organizations are mentioned related to layoffs?"

#GraphRAG questions
http ":8080/manualGraphRAG?question=What organizations are mentioned related to layoffs?"
http ":8080/manualGraphRAG?question=Which industries are most affected by the layoffs?"
http ":8080/manualGraphRAG?question=Are there new launch announcements in the news?"
http ":8080/manualGraphRAG?question=Which industries are seeing the most change?"
http ":8080/manualGraphRAG?question=What are the major funding announcements related to technology?"

##DEMO portion:

#Bad RAG question
http ":8080/manualGraphRAG?question=What is the latest news with Volkswagen?"
http ":8080/compare?question=What is the latest news with Volkswagen?"
#Good RAG questions
http ":8080/vectorRAG?question=Were there major investments made recently?"
http ":8080/manualGraphRAG?question=Are there organizations with major investments recently?"

#Agentic
#Vector agent
http ":8080/agents/agentic?question=What news is related to cybersecurity threats?"
http ":8080/agents/agentic?question=Are there any major funding announcements related to technology?"
#http ":8080/agents/agentic?question=What sentiment is in the articles?"
#http ":8080/agents/agentic?question=What articles mention AI?"
#Graph agent
http ":8080/agents/agentic?question=Which industries are seeing the most change?"
http ":8080/agents/agentic?question=Which organizations are dealing with layoffs?"
#http ":8080/agents/agentic?question=Which companies are in the technology industry?"
```

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- REST JSON-B ([guide](https://quarkus.io/guides/rest#json-serialisation)): JSON-B serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it.
- LangChain4j Neo4j embedding store ([guide](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)): Provides the Neo4j embedding store for Quarkus LangChain4j
- LangChain4j OpenAI ([guide](https://docs.quarkiverse.io/quarkus-langchain4j/dev/index.html)): Provides the basic integration with LangChain4j

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
