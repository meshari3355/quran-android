# القرآن الكريم - Android

تطبيق Android للقرآن الكريم ومواقيت الصلاة والقبلة والأذكار، مبني بـ Kotlin وJetpack Compose.

## المميزات

- قراءة القرآن الكريم.
- مواقيت الصلاة حسب الموقع.
- تنبيهات الصلاة قبل الوقت وفي الوقت.
- تذكيرات القراءة عند الانقطاع.
- القبلة.
- الأذكار.
- ويدجت الصلاة القادمة.

## المتطلبات

- JDK 17.
- Android Studio حديث أو Android SDK.
- Gradle Wrapper المرفق داخل المشروع.

## تشغيل المشروع

```bash
./gradlew :app:assembleDebug
```

ملف APK الناتج يظهر في:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## الخصوصية والأسرار

هذا المستودع لا يحتاج مفاتيح خاصة للبناء التجريبي. لا ترفع الملفات التالية إلى GitHub:

- `local.properties`
- ملفات التوقيع مثل `.jks` و `.keystore`
- `google-services.json`
- أي ملف `.env` أو أسرار API

## الترخيص

المشروع مرخص تحت MIT License.
