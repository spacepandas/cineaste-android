# Cineaste

An Android application to manage movies you would like to see and movies you have seen.
You can also start movie nights with your friends.

No need for registration!
No need to add friends!

Start a movie night via [Nearby][nearbyLink].

Nearby searches for nearby devices and matches their watchlist with yours.
As a result you can see which movie is the most interested one by you and your friends.

We are using [theMovieDb][theMovieDb].

Cineaste is released under the GPL V3 Open Source License. Please see the LICENSE file for more information.

## How to build

1. Get a [Nearby][nearbyLink] key.
2. Get a [theMovieDb] [theMovieDb] key.
3. Create `secrets.xml` in `res/values` and place your nearby and moviedb key inside it.
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
