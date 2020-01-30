# Cineaste

[![Build Status](https://travis-ci.com/spacepandas/cineaste-android.svg?branch=master)](https://travis-ci.org/spacepandas/cineaste-android.svg?branch=master)
[![code](https://img.shields.io/badge/code-Kotlin-blue.svg)]()
[![license](https://img.shields.io/badge/license-GPLv3-lightgrey.svg)](https://github.com/marcelgross90/Cineaste/blob/master/LICENSE)
[![platform](https://img.shields.io/badge/platform-android-lightgrey.svg)]()

An Android (and iOS) application to manage movies you would like to see and movies you have seen.

<a href='https://play.google.com/store/apps/details?id=de.cineaste.android&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/apps/en-play-badge.png' height="45px"/></a>

Check out the [iOS client](https://github.com/spacepandas/cineaste-ios) on GitHub. It is available on the App Store.

<a href='https://itunes.apple.com/us/app/cineaste-app/id1402748020'><img alt='Download on the App Store' img src='https://linkmaker.itunes.apple.com/assets/shared/badges/en-us/appstore-lrg.svg' width="152" height="45"/></a>

## Dependencies

We are using [theMovieDb][theMovieDb] to get access to the big movie data universe.

## How to build

1. Get a [theMovieDb][theMovieDb] key.
2. Create `secrets.xml` in `res/values` and place your moviedb key inside it.
```xml
    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <string name="movieKey">XXXX</string>
    </resources>
```
3. Continue with normal development or building process.

## License

Cineaste is released under the **GPL V3 Open Source License**. Please see the [LICENSE](https://github.com/marcelgross90/Cineaste/blob/master/LICENSE) file for more information.

[theMovieDb]: https://www.themoviedb.org/
