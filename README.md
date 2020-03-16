# Spellchecker Demo

A simple java program that launches a text pane with a text correction
popup and text highlighting for incorrect words.

### Configuration
The program is preconfigured for english using a large dictionary and sample file.

* The dictionary used by the spellchecker is loaded in from src/main/resources/words.txt,
which should consist of a valid all the valid words separated by newline characters. 

* Additionally, a sample text file (ideally quite large) must be provided at 
src/main/resources/sample.txt which will be used to estimate the relative probability of words. 

* An alphabet string must be provided at src/main/resources/alphabet.txt which should consist
of all characters that could occur in a valid word (including special characters) with no separation.

### Usage
Type or paste text into the window that opens, invalid words will be highlighted,
and corrections will be shown according to the cursor position. Use arrow keys to
change correction selection and the return key to accept a correction.