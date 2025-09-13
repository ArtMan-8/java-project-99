ifeq ($(OS),Windows_NT)
  SLASH='\'
else
  SLASH='/'
endif

GRADLEW=.$(SLASH)gradlew
BIN_APP=.$(SLASH)build$(SLASH)install$(SLASH)app$(SLASH)bin$(SLASH)app

.PHONY: build

lint: # Проверить кодстайл
	$(GRADLEW) checkstyleMain
	$(GRADLEW) checkstyleTest

clean: # Очистить дистрибутив
	$(GRADLEW) clean

build: clean # Установить зависимости и собрать дистрибутив
	$(GRADLEW) installDist

test: build # Собрать дистрибутив и запустить тесты
	$(GRADLEW) build

test-report: test # Подготовить покрытие тестов
	$(GRADLEW) jacocoTestReport

sonar: test-report
	$(GRADLEW) sonar --info

run: build # Запустить дистрибутив
	$(BIN_APP)
