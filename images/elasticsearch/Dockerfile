ARG ELASTICSEARCH_IMAGE=elasticsearch:7.17.28
FROM ${ELASTICSEARCH_IMAGE}

# Install the analysis-icu plugin
RUN bin/elasticsearch-plugin install --batch analysis-icu
# Install the analysis-smartcn plugin
RUN bin/elasticsearch-plugin install --batch analysis-smartcn
# Install the analysis-stempel plugin
RUN bin/elasticsearch-plugin install --batch analysis-stempel
# Install the analysis-kuromoji plugin
RUN bin/elasticsearch-plugin install --batch analysis-kuromoji
