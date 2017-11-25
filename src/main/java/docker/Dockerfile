# Set the base image
FROM centos:7

RUN yum -y update
RUN yum -y install wget
RUN wget https://copr.fedorainfracloud.org/coprs/omajid/openjdk9/repo/epel-7/omajid-openjdk9-epel-7.repo
RUN cp omajid-openjdk9-epel-7.repo /etc/yum.repos.d/
RUN yum install -y java-9-openjdk-devel

RUN yum install -y which
RUN curl https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh > wait.sh
RUN chmod +x wait.sh

COPY maven/ /usr/share/alexa-queries/


ENTRYPOINT ["./wait.sh", "database:3306", "--strict", "--", "java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005", "-jar", "/usr/share/alexa-queries/alexa-queries-1.0-SNAPSHOT-fat.jar"]