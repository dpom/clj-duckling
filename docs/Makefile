# Makefile
# 
# Converts org-mode to Markdown
#
# Run "make" (or "make all") to convert to all other formats
#
# Run "make clean" to delete converted files

# Convert all files in this directory that have a .md suffix
SOURCE_DOCS := $(wildcard *.org)

EXPORTED_DOCS= $(SOURCE_DOCS:.org=.md)


RM=/bin/rm

PANDOC=/usr/bin/pandoc

# PANDOC_OPTIONS=--smart --standalone
PANDOC_OPTIONS=--from org --to markdown_github --standalone

%.md : %.org
	$(PANDOC) $(PANDOC_OPTIONS) -o $@ $<

# Targets and dependencies

.PHONY: all clean

all : $(EXPORTED_DOCS)

clean:
	- $(RM) $(EXPORTED_DOCS)
