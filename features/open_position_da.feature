#language: da
Egenskab: Åbn position
  For at åbne en position
  Som en valutahandler
  Ønsker jeg at afgive en handelsordre

  Scenarie: Markedsordre
    Givet at min position i EURUSD er 0 købt til kurs 1,34700
    Og markedsprisen for EURUSD er [1,34662;1,34714]
    Når jeg afgiver en ordre om at KØBE 1000000 EURUSD til MARKEDSPRIS
    Så skal en handel ske til kurs 1,34714
    Og min position skal være LANG 1000000 EURUSD købt til kurs 1,34714
