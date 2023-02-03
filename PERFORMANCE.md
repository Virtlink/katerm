# Performance
We can try to figure out the best performance for this library.
For this we need representative projects that use ATerms and ITerms.

Then we can test the following:

- does it matter whether the terms are classes or interfaces
- does the if-unchanged check impact performance negatively or memory positively
- what is the memory usage
- should we allow in-place mutation? (ideally not)
- what is the performance impact of having custom builder lookup
- what is the performance benefit of strongly-typed Appl types