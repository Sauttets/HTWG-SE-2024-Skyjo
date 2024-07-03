FROM hseeberger/scala-sbt:17.0.2_1.6.2_3.1.1
WORKDIR /Skyjo
RUN apt-get update && \
  apt-get install -y libxext6 libxrender1 libxtst6 libxi6 libxrandr2
ADD . /Skyjo/
CMD sbt run
