# Cineaste

[![code](https://img.shields.io/badge/code-Java-orange.svg)]()
[![license](https://img.shields.io/badge/license-GPLv3-lightgrey.svg)](https://github.com/marcelgross90/Cineaste/blob/master/LICENSE)
[![platform](https://img.shields.io/badge/platform-android-lightgrey.svg)]()

An Android (and iOS) application to manage movies you would like to see and movies you have seen.
You can also start movie nights with your friends. There is no need to register or to add friends.

<a href='https://play.google.com/store/apps/details?id=de.cineaste.android&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png' height="45px"/></a>

Check out the [iOS client](https://github.com/ChristianNorbertBraun/Cineaste) on github. It is still in development.

## Dependencies

You can start a movie night via [Nearby][nearbyLink]. Nearby searches for nearby devices and matches their watchlist with yours.
As a result you can see which movie is the most interested one by you and your friends.

We are using [theMovieDb][theMovieDb] to get access to the big movie data universe.

## How to build

1. Get a [Nearby][nearbyLink] key.
2. Get a [theMovieDb][theMovieDb] key.
3. Create `secrets.xml` in `res/values` and place your nearby and moviedb key inside it.
```xml
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="movieKey">XXXX</string>
        <string name="nearbyKey">XXXX</string>
    </resources>
```
4. Continue with normal development or building process.

## License

Cineaste is released under the **GPL V3 Open Source License**. Please see the [LICENSE](https://github.com/marcelgross90/Cineaste/blob/master/LICENSE) file for more information.

[nearbyLink]: https://developers.google.com/nearby/messages/overview
[theMovieDb]: https://www.themoviedb.org/
