MAVEN	:= mvn
MVN_FLAGS	:= -B -ntp
POM_FILE	:= project-microservice/pom.xml

.PHONY: clean init test build

clean:
	$(MAVEN) $(MVN_FLAGS) clean -f $(POM_FILE)

init:
	$(MAVEN) $(MVN_FLAGS) dependency:go-offline -f $(POM_FILE)

test:
	$(MAVEN) $(MVN_FLAGS) verify -f $(POM_FILE)

build:
	$(MAVEN) $(MVN_FLAGS) package -DskipTests -f $(POM_FILE)
	$(MAVEN) $(MVN_FLAGS) jacoco:report -f $(POM_FILE)
