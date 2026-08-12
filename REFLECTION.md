# Individual Reflection

Building GameFlix taught me more about steering a project than about writing any
single class. The hardest part was not the code — it was that the repo had drifted.
I had Course and Student CRUD screens, a Movie example I'd used to learn testing,
and a half-finished auth layer, none of which matched what GameFlix was supposed to
be. Pulling that apart and rebuilding it into a coherent UserAccount / Game /
Subscription domain, without breaking the auth and Docker pieces that were fine, was
the real work. It forced me to actually understand every file before deleting it.

AI assistance helped most in two places. The first was scaffolding: generating DTOs,
repository interfaces, and Thymeleaf templates that would have taken me an evening of
boilerplate each. The second was the JWT layer. I had used BCrypt before but never
issued and verified tokens, and having a working JwtService and filter to start from
meant I could spend my time understanding *why* it worked rather than fighting the
library's API from a blank file.

It also misled me, and those moments were the most useful. One suggestion put the
password hashing in the controller instead of the service, which would have leaked
plaintext into the wrong layer. Another generated a subscribe method that let a user
accumulate multiple active subscriptions every time they clicked the button — it
compiled and looked right, and I only caught it because I checked the row count in
the database. The JWT code came with a signing secret too short for the algorithm,
and unauthenticated API calls redirected to a login page instead of returning 401.
Every one of these passed a casual read and failed the moment I actually tested it.

That's the skill I think still matters most. Generated code is fluent, which makes it
easy to trust, so the burden shifts to reading critically, knowing what correct
behavior looks like, and writing the test that proves it. Understanding layering,
security fundamentals, and how to verify a claim did not get less important — they
got more important, because they're now the only thing standing between a plausible
answer and a correct one. The tool made me faster; my judgment is what made the
result trustworthy.
