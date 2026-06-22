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
http ":8080//llm?question=Who is Jennifer Reif using this as context? Jennifer Reif is a developer advocate at Neo4j, focusing on the Java ecosystem. She is a technical speaker, blogger, podcaster, and author, with an MS in CMIS."
http ":8080/llm?question=Which companies recently announced major layoffs?"
http ":8080/llm?question=What cybersecurity threats are being discussed in current news?"

#Vector RAG questions
http ":8080/vectorRAG?question=Which companies recently announced major layoffs?"
http ":8080/vectorRAG?question=What cybersecurity threats are being discussed in the news?"

#GraphRAG questions
http ":8080/manualGraphRAG?question=Which companies recently announced major layoffs?"
http ":8080/manualGraphRAG?question=Which industries are most affected by layoffs?"

##DEMO portion:

#Bad RAG question
http ":8080/manualGraphRAG?question=What is the latest news with Volkswagen?"
http ":8080/compare?question=What is the latest news with Volkswagen?"
#Good RAG questions
http ":8080/vectorRAG?question=Were there major investments made recently?"
http ":8080/manualGraphRAG?question=Are there organizations with major investments recently?"

#Agentic
http ":8080/agents/debug/tools"
##Vector agent
http ":8080/agents/agentic?question=What news is related to cybersecurity threats?"
##Graph agent
http ":8080/agents/agentic?question=Which industries are seeing the most change recently?"
##Text2Cypher agent
http ":8080/agents/agentic?question=Which organizations are not public and have revenue over 50 billion?"
http ":8080/agents/agentic?question=Where is JD.com organization located?"
http ":8080/agents/agentic?question=How many employees does Amazon have?"
http ":8080/agents/agentic?question=Which organizations have the most article mentions and what sectors are they in?"
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
