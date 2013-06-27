package com.github.davidmoten.geo;

import static com.github.davidmoten.geo.Base32.decodeBase32;
import static com.github.davidmoten.geo.Base32.encodeBase32;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class Base32Test {

    @Test
    public void testEncodePositiveInteger() {
        assertEquals("15pn7", encodeBase32(1234567));
    }

    @Test
    public void testEncodesZero() {
        assertEquals("0", encodeBase32(0));
    }

    @Test
    public void testEncodesNegativeInteger() {
        assertEquals("-3v", encodeBase32(-123));
    }

    @Test
    public void testDecodeToPositiveInteger() {
        assertEquals(1234567, decodeBase32("15pn7"));
    }

    @Test
    public void testDecodeToZero() {
        assertEquals("0", encodeBase32(0));
        assertEquals(0, decodeBase32("0"));
    }

    @Test
    public void testDecodeToNegativeInteger() {
        assertEquals("-3v", encodeBase32(-123));
        assertEquals(-123, decodeBase32("-3v"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCharIndexThrowsExceptionWhenNonBase32CharacterGiven() {
        Base32.getCharIndex('?');
    }

    @Test
    public void getCoverageOfConstructorAndCheckConstructorIsPrivate() {
        TestingUtil.callConstructorAndCheckIsPrivate(Base32.class);
    }

}
