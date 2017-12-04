Cineaste
==============

An Android Application to manage movies you would like to see and movies you have seen. 
You can also start movienights with your friends.

No need for registration!
No need for adding friends!

Start a Movienight via [Nearby][nearbyLink].

Nearby searches for people nearby and matches their watchlist entries with yours.
As a result you can see which movie is the most interested one by your friends.

We are using [theMovieDb][theMovieDb].

Cineaste is released under the GPL V3 Open Source License. Please se LICENSE file for more information.

How to
------

1. Get a [Nearby][nearbyLink] key.
2. Get a [theMovieDb] [theMovieDb] key.
3. Create a `secrets.xml` in `res/values` and place your nearby and moviedb key inside it.
```
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="movieKey">XXXX</string>
        <string name="nearbyKey">XXXX</string>
    </resources>
```
4. Continue with normal development, or building process.


[nearbyLink]: https://developers.google.com/nearby/messages/overview
[theMovieDb]: https://www.themoviedb.org/
