mvn clean package bundle:bundle 
cp pom.xml ../repo/io/cresco/library/1.0-SNAPSHOT/library-1.0-SNAPSHOT.pom
cp target/library-1.0-SNAPSHOT.jar ../repo/io/cresco/library/1.0-SNAPSHOT
cp target/library-1.0-SNAPSHOT.jar ../agent/src/main/resources

#cp pom.xml /Users/vcbumg2/ResearchWorx/repo/io/cresco/library/1.0-SNAPSHOT/library-1.0-SNAPSHOT.pom
#cp target/library-1.0-SNAPSHOT.jar /Users/vcbumg2/ResearchWorx/repo/io/cresco/library/1.0-SNAPSHOT
