# Makefile for the US2001

# variables
SOURCES = applicationsFileBot.c
OBJFILES = applicationsFileBot.o
EXEC = applicationsFileBot

# Suffix rules
.SUFFIXES : .c .o

# How to build an object .o from a code file .c ; $< -- file name
.c.o:
		gcc-12 -g -Wall -Wextra -fanalyzer -c -lpthread -lrt $<

${EXEC}: ${OBJFILES}
		gcc-12 ${OBJFILES} -o ${EXEC}

${OBJFILES}: ${SOURCES}

run: ${EXEC}
		./${EXEC} ${ARGS}

clean:
		rm -f ${OBJFILES} ${EXEC}
