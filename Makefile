

TEST_INPUT_SRCDIR = test-input
TEST_INPUT_SRC := $(shell find $(TEST_INPUT_SRCDIR) -name '*.java')
TEST_INPUT_JAR = out/jars/test-input.jar
TEST_INPUT_CLASSDIR = $(TEST_INPUT_JAR:out/jars/%.jar=out/classes/%)

all: $(TEST_INPUT_JAR) headers

$(TEST_INPUT_JAR): $(TEST_INPUT_SRC)
	@mkdir -p $(dir $@)
	@mkdir -p $(TEST_INPUT_CLASSDIR)
	javac -d $(TEST_INPUT_CLASSDIR) -s $(TEST_INPUT_SRCDIR) $^
	jar -cf0 $@ -C $(TEST_INPUT_CLASSDIR) ./

.PHONY: headers
headers: $(TEST_INPUT_JAR)
	@mkdir -p out/headers
	javah -d out/headers -classpath $< $(subst /,.,$(TEST_INPUT_SRC:$(TEST_INPUT_SRCDIR)/%.java=%))

.PHONY: clean
clean:
	rm -rf out/
