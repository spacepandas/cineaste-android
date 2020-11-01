PACKAGE = de.cineaste.android

all: debug install start

cleanInstall: release uninstallRelease installRelease start

debug:
	./gradlew assembleDebug

lint:
	./gradlew lintDebug

release: lint
	@./gradlew assembleRelease \
		-Pandroid.injected.signing.store.file=$(ANDROID_KEYFILE) \
		-Pandroid.injected.signing.store.password=$(ANDROID_STORE_PASSWORD) \
		-Pandroid.injected.signing.key.alias=$(ANDROID_KEY_ALIAS) \
		-Pandroid.injected.signing.key.password=$(ANDROID_KEY_PASSWORD)

install:
	adb $(TARGET) install -r app/build/outputs/apk/debug/app-debug.apk

installRelease:
	adb $(TARGET) install -r app/build/outputs/apk/release/app-release.apk

start:
	adb $(TARGET) shell 'am start -n $(PACKAGE).debug/$(PACKAGE).MainActivity'

uninstall:
	adb $(TARGET) uninstall $(PACKAGE).debug

uninstallRelease:
	adb $(TARGET) uninstall $(PACKAGE)

clean:
	./gradlew clean

format: 
	./gradlew ktlintFormat

test:
	./gradlew test
