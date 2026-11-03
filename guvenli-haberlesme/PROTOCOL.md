# Haberleşme Protokolü

**Sürüm:** 1.0

**Tarih:** 03.11.2026

Bu sürüm, sunucu üzerinden şifresiz metin gönderimini tanımlar. Sürüm numarası yalnızca belgededir; mesajlarda gönderilmez.

## Temel kurallar

- Bağlantı: **TCP**, port: **1881**, karakter kodlaması: **UTF-8**.
- Her mesaj tek satırdır. Sonuna satır sonu eklenir ve `flush()` çağrılır.
- Alanlar `|` ile ayrılır. Mesaj metni `|` içerebilir, ancak satır sonu içeremez.
- Kullanıcı adı 1-32 karakterdir; yalnızca İngilizce harf, rakam, `_` ve `-` içerebilir. Aynı anda iki istemci aynı adı kullanamaz.
- Komutlar ve kullanıcı adları büyük/küçük harfe duyarlıdır.

## 1. Kayıt

İstemci ilk olarak kullanıcı adını gönderir:

```text
HELLO|yagiz
```

Sunucu kabul ederse şu yanıtı verir:

```text
WELCOME|yagiz
```

İstemci, mesaj göndermeden önce bu yanıtı bekler.

## 2. Mesaj gönderme

yagiz, kayıtlı olan engin'e mesaj gönderir:

```text
TEXT|engin|Merhaba
```

Sunucu mesajı ekrana yazar ve engin'e iletir:

```text
FROM|yagiz|Merhaba
```

Gönderici adı, sunucudaki kayıttan alınır.

## 3. Bağlantıyı kapatma

İstemci şu komutu gönderir:

```text
QUIT
```

Sunucu bağlantıyı kapatır ve kullanıcı kaydını siler. Yanıt göndermez.

## Hatalar

Hata mesajının biçimi: `ERROR|HATA_KODU`

| Hata kodu | Anlamı |
| --- | --- |
| EXPECTED_HELLO | İlk mesaj HELLO olmalı. |
| INVALID_CLIENT_ID | Kullanıcı adı kurallara uygun değil. |
| ID_ALREADY_USED | Kullanıcı adı zaten kullanılıyor. |
| EXPECTED_TEXT | Mesajın biçimi TEXT, alıcı ve metin olmalı. |
| RECIPIENT_NOT_AVAILABLE | Alıcı bağlı değil veya kişi kendisine gönderiyor. |
| FORWARD_FAILED | Mesaj alıcıya iletilemedi. |

Yukarıdaki ilk üç hatada bağlantı kapatılır. Diğerlerinde bağlantı kullanılmaya devam edilebilir. Bağlantı koparsa hata yanıtı ulaşmayabilir.

Bu sürümde dosya gönderimi ve şifreleme yoktur.