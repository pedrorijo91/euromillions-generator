# Euromillions-generator [![Codacy Badge](https://www.codacy.com/project/badge/b19ce051b63c42eb95f2cf499485c11c)](https://www.codacy.com)

##Euromillions
[Euromillions](http://www.euro-millions.com/) is a European lottery that takes place on Tuesday and Friday evenings. Whereas national lotteries are generally limited to the residents of one particular country, the EuroMillions lottery pools the stakes to create huge jackpots and prizes. As the main prize pool can roll over to the next draw if there is no jackpot winner, prizes can, after a few weeks without a winner, be as high as €190 million.

##Euromillions random bet generator.
Generate random bets for [Euromillions](http://www.euro-millions.com/).

###How to run
Just use the command `sbt run` and you will get 5 random numbers, and 2 random stars.

###Using lucky numbers
Do you have some lucky numbers that you want to choose everytime? Then just pass them in the command line. Use `-n NUMBER`or `--number NUMBER` to pre-select numbers, and `-s STAR`or `--star STAR` to pre-select stars.

Examples:

`sbt "run -n 3"` or `sbt "run --number 3"` would produce a list of 4 random numbers, plus number `3`, and a list of 2 random stars.

`sbt "run -s 5"` or `sbt "run --star 5"` would produce a list of 1 random star, plus star `5`, and a list of 5 random numbers.

`sbt "run -n 1 -n 2 -n 3 -n 4"` would produce a list containing numbers `1,2,3,4` plus another random number, and a list with 2 random stars.

`sbt "run -s 3 -s 15"` would produce a list of 1 random star plus star `3` (because `15` is an invalid star), and a list of 5 random numbers.

`sbt "run -s 15 -s 3 -s 30 -s 1 -s 4"` would produce a random list of 5 numbers, and return `1,3` as the list of stars (limited to max number of stars after removing invalid choices).

> If you pass invalid numbers/stars, those will be ignored.

> If you pass more than allowed number of numbers/stars (5/2), then only the first ones will be considered.

> Numbers and stars are first filtered by valid choices, and then limited.

##Draw results checker
Alternatively, it is also possible to check for last draw results. All you need to do is to add a flag:
`sbt "run -p"` or `sbt "run --prize"`

Additionally you can provide your ticket(s) and it will check if you've won something, and how much.

In `src/main/resources/application.conf.example` you can see how to provide required configurations:

* `euromillions.results.api.key` is the [mashape](https://www.mashape.com/) key for fetching draw data through [euromillions endpoint](https://www.mashape.com/creativesolutions/euromillions#findlast)
* your tickets are provided as a list of numbers and a list of stars, as you may see in the [configuration example](https://github.com/pedrorijo91/euromillions-generator/blob/master/src/main/resources/application.conf.example)

> configuration file parsed with [typesafehub/config](https://github.com/typesafehub/config) using [HOCON](https://github.com/typesafehub/config/blob/master/HOCON.md) format
