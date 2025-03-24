# [Choice] Alpine version: 3.16, 3.15, 3.14, 3.13
FROM mcr.microsoft.com/vscode/devcontainers/base:alpine-3.20

##### Create java 11 instalation
ENV LANG="C.UTF-8"
ENV JAVA_HOME="/usr/lib/jvm/java-17-openjdk/"
ENV JDK_HOME="/usr/lib/jvm/java-17-openjdk/"
ENV PATH="$PATH:/usr/lib/jvm/java-17-openjdk/jre/bin:/usr/lib/jvm/java-17-openjdk/bin"

RUN set -x \
	&& apk add --no-cache \
		openjdk17 \
		openjdk21 \
		maven \
		vim \
		inotify-tools

# Add dummy jrt-fs, because vscode-java would not recognize is otherwise.
RUN touch /usr/lib/jvm/java-17-openjdk/lib/jrt-fs.jar

