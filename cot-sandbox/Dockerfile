# Dockerfile for COT Sandbox - Kotlin Script Execution Environment
# This Dockerfile creates a lightweight environment for safely executing
# single-file Kotlin scripts with the COT DSL as a dependency.

FROM eclipse-temurin:17-jdk-alpine

# Install Kotlin compiler
ENV KOTLIN_VERSION=2.2.21
ENV KOTLIN_HOME=/opt/kotlin

RUN apk add --no-cache curl unzip && \
    curl -L "https://github.com/JetBrains/kotlin/releases/download/v${KOTLIN_VERSION}/kotlin-compiler-${KOTLIN_VERSION}.zip" -o kotlin-compiler.zip && \
    unzip kotlin-compiler.zip -d /opt && \
    rm kotlin-compiler.zip && \
    mv /opt/kotlinc ${KOTLIN_HOME} && \
    apk del curl unzip

ENV PATH="${KOTLIN_HOME}/bin:${PATH}"

# Create directories for script execution
RUN mkdir -p /sandbox/libs /sandbox/scripts /sandbox/output

WORKDIR /sandbox

# The script file and dependencies will be mounted at runtime
# Usage:
#   docker run -v /path/to/script.kts:/sandbox/scripts/script.kts \
#              -v /path/to/cot-dsl.jar:/sandbox/libs/cot-dsl.jar \
#              cot-sandbox kotlinc -script scripts/script.kts -classpath libs/cot-dsl.jar

CMD ["sh", "-c", "echo 'COT Sandbox ready. Mount script and execute with kotlinc.'"]
