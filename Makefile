

JDK := $(abspath $(dir $(abspath $(dir $(shell which javah)))))

TEST_INPUT_SRCDIR = test-input
TEST_INPUT_SRC := $(shell find $(TEST_INPUT_SRCDIR) -name '*.java')
TEST_INPUT_JAR = out/jars/test-input.jar
TEST_INPUT_CLASSDIR = $(TEST_INPUT_JAR:out/jars/%.jar=out/classes/%)

.PHONY: all
all: $(TEST_INPUT_JAR) check

$(TEST_INPUT_JAR): $(TEST_INPUT_SRC)
	@mkdir -p $(dir $@)
	@mkdir -p $(TEST_INPUT_CLASSDIR)
	javac -d $(TEST_INPUT_CLASSDIR) -s $(TEST_INPUT_SRCDIR) $^
	jar -cf0 $@ -C $(TEST_INPUT_CLASSDIR) ./

out/obj/test.o: $(TEST_INPUT_JAR) test/com/natpryce/jnirn/JNIRegisterNativesTest.testGeneratedCode.c
	@mkdir -p out/headers
	javah -d out/headers -classpath $< $(subst /,.,$(TEST_INPUT_SRC:$(TEST_INPUT_SRCDIR)/%.java=%))
	@mkdir -p $(dir $@)
	gcc -Werror -Wall -I $(JDK)/include -I $(JDK)/include/linux  -I out/headers -c -o $@ test/com/natpryce/jnirn/JNIRegisterNativesTest.testGeneratedCode.c

.PHONY: check
check: out/obj/test.o

.PHONY: clean
clean:
	rm -rf out/
