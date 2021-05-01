# CSE-151-Project5 -Machine Learning Boosting
In this assignment, we will look at the task of spam classification using boosting. Our raw data is a set
of emails, which were collected from a liguistics mailing list; the emails are labeled as spam or not spam.
For your benefit, we have already preprocessed the emails to remove stop-words, punctuation, and to do
some preliminary preprocessing that lemmatises the words (for example, that maps words such as include,
includes and included to the same word), and converted them to vectors of features.
Download files pa5train.txt, pa5test.txt and pa5dictionary.txt from the class website. The first
two files contain your training and test datasets respectively. The third file is a dictionary and contains a
list of words. Each line in the files pa5train.txt and pa5test.txt correspond to an email followed a label
which can be 1 or −1. An email is represented by a feature vector of length 4003; a label 1 indicates that
the email is a spam message, and a label −1 indicates that it is not spam. Coordinate i of the feature vector
corresponding to an email is 1 when word i in pa5dictionary.txt is present in the email and 0 otherwise.
1. Write down the training and test errors of the classifiers obtained after t = 3, 7, 10, 15, 20 rounds of
boosting. Use the following weak learning procedure. Each weak learner corresponds to a classifier
hi,+ or hi,−, where i is a word in the dictionary and the classifier hi,+ is the rule:
hi,+(x) = 1, if word i occurs in email x
= −1, otherwise
Similarly, the classifier hi,− is the rule:
hi,−(x) = 1, if word i does not occur in email x
= −1, otherwise
The set of weak learners C is the collection of such classifiers for all i, and your weak learning procedure
should select the weak learner which has the highest accuracy in C with respect to the current weighted
set of examples.
2. Based on the dictionary file, write down the words corresponding to the weak learners chosen in the
first 10 rounds of boosting.
[Hint: If your code is correct, you should get a training error of 0.051 and a test error of 0.039 after 4
rounds of boosting.]
