package peergos.shared.user.fs;

import peergos.shared.crypto.*;
import peergos.shared.crypto.symmetric.*;
import peergos.shared.util.*;

import java.util.*;

public class EncryptedChunk {

    private final byte[] auth, cipher;

    public EncryptedChunk(byte[] auth, byte[] cipher) {
        this.auth = auth;
        this.cipher = cipher;
    }

    public EncryptedChunk(byte[] encrypted) {
        this(Arrays.copyOfRange(encrypted, 0, TweetNaCl.SECRETBOX_OVERHEAD_BYTES),
        Arrays.copyOfRange(encrypted, TweetNaCl.SECRETBOX_OVERHEAD_BYTES, encrypted.length));
    }

    public List<Fragment> generateFragments(peergos.shared.user.fs.Fragmenter  fragmenter) {
        if (this.cipher.length == 0)
            return Collections.emptyList();

        byte[][] bfrags = fragmenter.split(cipher);
        List<Fragment> frags = new ArrayList<>();
        for (int i=0; i < bfrags.length; i++)
            frags.add(new Fragment(bfrags[i]));
	    return frags;
    }

    public byte[] getAuth() {
        return Arrays.copyOf(auth, auth.length);
    }

    public byte[] decrypt(SymmetricKey key, byte[] nonce) {
        if (cipher.length == 0)
            return cipher;
        return key.decrypt(ArrayOps.concat(this.auth, this.cipher), nonce);
    }

    public EncryptedChunk truncateTo(int length) {
        return new EncryptedChunk(auth, Arrays.copyOfRange(cipher, 0, length));
    }
}
