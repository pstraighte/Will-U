cd /usr/local/tomcat
bin/shutdown.sh
cd webapps 
rm -rf ROOT/
cp ~/cicdtest/test-app.war ./ROOT.war
cd ..
bin/startup.sh
