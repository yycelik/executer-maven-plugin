# executer-maven-plugin

mvn executer:remote-code-executer -Dhostname="xxx.xxx.xxx.xxx" -Dusername="xxx" -Dpassword="xxx" -Dcmd="echo 'xxx'" 

mvn executer:remote-bash-executer -Dhostname="xxx.xxx.xxx.xxx" -Dusername="xxx" -Dpassword="xxx"



#run mvn in debug mode
mvnDebug executer:remote-code-executer -Dhostname="xxx.xxx.xxx.xxx" -Dusername="xxx" -Dpassword="xxx" -Dcmd="echo 'xxx'" 

#run eclipse remote debug mode. 
#default port is 8000
