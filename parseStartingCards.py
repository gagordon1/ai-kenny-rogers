import re
import eval7
f = open("startingcards.txt", "r")
#c1 an eval7 card object
#c2 an eval7 card object
def getScore(c1, c2, iters = 10000):
	score = 0
	for i in range(iters):
		deck = eval7.Deck()
		deck.shuffle()
		myHand = [c1,c2]

		d = deck.deal(52)
		d.remove(c1)
		d.remove(c2)

		oppHand = []
		for i in range(2):
			oppHand.append(d.pop(0))

		street = []
		for i in range(5):
			street.append(d.pop(0))

		myHand = myHand + street
		oppHand = oppHand + street

		if eval7.evaluate(myHand) > eval7.evaluate(oppHand):
			score += 1

	return score/iters

#cards maps cards to their EV according to website
suitedCards = {}
nonSuitedCards = {}
hands = []
hand = []
for line in f:
	print(line)
	if "class=\"boldgreen\"" in line:
		#card line
		print("CARD LINE")
		target = line[50:54]
		# if " s" in target:
		# 	t = target.replace(" s", "")
		# 	hand.append(t)

		# elif "</" in target:
		# 	t = target.replace("</","")
		# 	hand.append(t)

		# print(hand)
		hand.append(target)
	
	if "class=\"green\"" in line:
		#EV line
		print("EV LINE")
		print(line)
		target = line[80:85]
		if "<" in target:
			t = target.replace("<", "")
			hand.append(t)
		else:

			hand.append(target)
		hands.append(list(hand))
		hand = []
	print("----")

for h in hands:
	card = h[0]
	value = h[1]
	v = float(value)
	if "</" in card:
		c = card.replace("</","")
		nonSuitedCards[c] = v
	else:
		c = card.replace(" s","")
		c = c.replace("s<", "")
		suitedCards[c] = v

#GET WIN PROBABILITIES FOR EACH SUITED CARD
for cards in suitedCards:

	suit = "s"
	card1 = eval7.Card(cards[0] + suit)
	card2 = eval7.Card(cards[1] + suit)
	score = getScore(card1,card2)
	print(card1, card2)
	print(score)
	print('\n'*2)
	suitedCards[cards] = score

for cards in nonSuitedCards:
	suit1 = "s"
	suit2 = "c"

	card1 = eval7.Card(cards[0] + suit1)
	card2 = eval7.Card(cards[1] + suit2)
	score = getScore(card1,card2)

	print(card1, card2)
	print(score)
	print('\n'*2)
	nonSuitedCards[cards] = score

#SUITED CARDS
for cards in suitedCards:
	print("\"" + cards +"\""+ ",")

print("\n"*3)

#CARD WIN PROBS
for cards in suitedCards:
	print(str(suitedCards[cards]) + ",")

print("\n"*3)

#NONSUITED CARDS
for cards in nonSuitedCards:
	print("\"" + cards +"\""+ ",")

print("\n"*3)

#NONSUITED CARD WIN PROBS
for cards in nonSuitedCards:
	print(str(nonSuitedCards[cards]) + ",")		


#suitedCards
#suitedCardWinProb

	









	
