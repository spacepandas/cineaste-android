PACKAGE = de.cineaste.android
APK = app/build/outputs/apk/app-debug.apk
DEPENDENCIES = \
	"kta-camera-preview-android" \
	"kta-inigma-android" \
	"kta-mobile-sdk-android"

all: debug install start

debug:
	./gradlew assembleDebug

lint:
	./gradlew lintDebug

release:
	@./gradlew assembleRelease \
		-Pandroid.injected.signing.store.file=$(ANDROID_KEYFILE) \
		-Pandroid.injected.signing.store.password=$(ANDROID_STORE_PASSWORD) \
		-Pandroid.injected.signing.key.alias=$(ANDROID_KEY_ALIAS) \
		-Pandroid.injected.signing.key.password=$(ANDROID_KEY_PASSWORD)

install:
	adb $(TARGET) install -rk $(APK)

start:
	adb $(TARGET) shell 'am start -n $(PACKAGE)/.MainActivity'

uninstall:
	adb $(TARGET) uninstall $(PACKAGE)

update: $(DEPENDENCIES)

$(DEPENDENCIES):
	git remote | grep $@ &>/dev/null ||\
		git remote add $@ \
			https://git.adorsys.de/kta/$@.git
	git fetch --no-tags $@
	git merge -s subtree --squash $@/master

clean:
	./gradlew clean
