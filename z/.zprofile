export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_231.jdk/Contents/Home
export SBT_HOME=/Users/objektwerks/sbt
export HADOOP_HOME=/usr/local/Cellar/hadoop/3.2.1/libexec
export HBASE_HOME=/usr/local/Cellar/hbase/1.3.5/libexec
export HIVE_HOME=/usr/local/Cellar/hive/3.1.2/libexec
export SPARK_HOME=/usr/local/Cellar/apache-spark/2.4.4/libexec
export PATH=/usr/local/bin:/usr/local/sbin:/usr/bin:/usr/sbin:$JAVA_HOME/bin:$SBT_HOME/bin:$HADOOP_HOME/bin:$HADOOP_HOME/sbin:$HBASE_HOME/bin:$HIVE_HOME/bin:$SPARK_HOME/bin:$SPARK_HOME/sbin:$PATH

export SBT_OPTS="-Xms1g -Xmx4G -Xss2M -XX:ReservedCodeCacheSize=128m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=1G -XX:MaxMetaspaceSize=2G -XX:+CMSClassUnloadingEnabled"

alias hstart=$HADOOP_HOME/sbin/start-all.sh
alias hstop=$HADOOP_HOME/sbin/stop-all.sh