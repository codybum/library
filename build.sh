mvn clean package bundle:bundle 
cp pom.xml ../repo/io/cresco/library/1.0-SNAPSHOT/library-1.0-SNAPSHOT.pom
cp target/library-1.0-SNAPSHOT.jar ../repo/io/cresco/library/1.0-SNAPSHOT
cp target/library-1.0-SNAPSHOT.jar ../agent/src/main/resources


#mvn clean package bundle:bundle 
#cp target/library-1.0-SNAPSHOT.jar /Users/cody/IdeaProjects/agent/jars 
#cp target/library-1.0-SNAPSHOT.jar /Users/cody/IdeaProjects/felix-framework-5.6.10/bundle
#cp pom.xml /Users/cody/IdeaProjects/repo/io/cresco/library/1.0-SNAPSHOT/library-1.0-SNAPSHOT.pom
#cp target/library-1.0-SNAPSHOT.jar /Users/cody/IdeaProjects/repo/io/cresco/library/1.0-SNAPSHOT
