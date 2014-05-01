/*
 * Copyright (C) 2009-2011 Mathias Doenitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.parboiled.common;

import com.google.common.io.Files;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

import static org.parboiled.common.Preconditions.checkArgNotNull;

/**
 * Deprecated!
 *
 * @deprecated use {@link Files} instead
 */
@Deprecated
public final class FileUtils {

    private FileUtils() {}

    public static String readAllTextFromResource(final String resource) {
        checkArgNotNull(resource, "resource");
        return readAllText(FileUtils.class.getClassLoader().getResourceAsStream(resource));
    }

    public static String readAllTextFromResource(
        final String resource, final Charset charset) {
        checkArgNotNull(resource, "resource");
        checkArgNotNull(charset, "charset");
        return readAllText(FileUtils.class.getClassLoader().getResourceAsStream(resource), charset);
    }

    public static String readAllText(final String filename) {
        checkArgNotNull(filename, "filename");
        return readAllText(new File(filename));
    }

    public static String readAllText(final String filename, final Charset charset) {
        checkArgNotNull(filename, "filename");
        checkArgNotNull(charset, "charset");
        return readAllText(new File(filename), charset);
    }

    public static String readAllText(final File file) {
        checkArgNotNull(file, "file");
        return readAllText(file, Charset.forName("UTF8"));
    }

    public static String readAllText(final File file, final Charset charset) {
        checkArgNotNull(file, "file");
        checkArgNotNull(charset, "charset");
        try {
            return readAllText(new FileInputStream(file), charset);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }

    public static String readAllText(final InputStream stream) {
        return readAllText(stream, Charset.forName("UTF8"));
    }

    public static String readAllText(final InputStream stream, final Charset charset) {
        checkArgNotNull(charset, "charset");
        if (stream == null) return null;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
        final StringWriter writer = new StringWriter();
        copyAll(reader, writer);
        return writer.toString();
    }
    
    public static char[] readAllCharsFromResource(final String resource) {
        checkArgNotNull(resource, "resource");
        return readAllChars(FileUtils.class.getClassLoader().getResourceAsStream(resource));
    }

    public static char[] readAllCharsFromResource(
        final String resource, final Charset charset) {
        checkArgNotNull(resource, "resource");
        checkArgNotNull(charset, "charset");
        return readAllChars(FileUtils.class.getClassLoader().getResourceAsStream(resource), charset);
    }

    public static char[] readAllChars(final String filename) {
        checkArgNotNull(filename, "filename");
        return readAllChars(new File(filename));
    }

    public static char[] readAllChars(final String filename, final Charset charset) {
        checkArgNotNull(filename, "filename");
        checkArgNotNull(charset, "charset");
        return readAllChars(new File(filename), charset);
    }

    public static char[] readAllChars(final File file) {
        checkArgNotNull(file, "file");
        return readAllChars(file, Charset.forName("UTF8"));
    }

    public static char[] readAllChars(final File file, final Charset charset) {
        checkArgNotNull(file, "file");
        checkArgNotNull(charset, "charset");
        try {
            return readAllChars(new FileInputStream(file), charset);
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }

    public static char[] readAllChars(final InputStream stream) {
        return readAllChars(stream, Charset.forName("UTF8"));
    }

    public static char[] readAllChars(final InputStream stream, final Charset charset) {
        checkArgNotNull(charset, "charset");
        if (stream == null) return null;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
        final CharArrayWriter writer = new CharArrayWriter();
        copyAll(reader, writer);
        return writer.toCharArray();
    }

    public static byte[] readAllBytesFromResource(final String resource) {
        checkArgNotNull(resource, "resource");
        return readAllBytes(FileUtils.class.getClassLoader().getResourceAsStream(resource));
    }

    public static byte[] readAllBytes(final String filename) {
        checkArgNotNull(filename, "filename");
        return readAllBytes(new File(filename));
    }

    public static byte[] readAllBytes(final File file) {
        checkArgNotNull(file, "file");
        try {
            return readAllBytes(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            return null;
        }
    }

    public static byte[] readAllBytes(final InputStream stream) {
        if (stream == null) return null;
        final BufferedInputStream in = new BufferedInputStream(stream);
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        copyAll(in, out);
        return out.toByteArray();
    }

    public static void writeAllText(final String text, final String filename) {
        checkArgNotNull(filename, "filename");
        writeAllText(text, new File(filename));
    }

    public static void writeAllText(final String text, final String filename, final Charset charset) {
        checkArgNotNull(filename, "filename");
        checkArgNotNull(charset, "charset");
        writeAllText(text, new File(filename), charset);
    }

    public static void writeAllText(final String text, final File file) {
        checkArgNotNull(file, "file");
        writeAllText(text, file, Charset.forName("UTF8"));
    }

    public static void writeAllText(
        final String text, final File file, final Charset charset) {
        checkArgNotNull(file, "file");
        checkArgNotNull(charset, "charset");
        try {
            ensureParentDir(file);
            writeAllText(text, new FileOutputStream(file), charset);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeAllText(final String text, final OutputStream stream) {
        checkArgNotNull(stream, "stream");
        writeAllText(text, stream, Charset.forName("UTF8"));
    }

    public static void writeAllText(final String text, final OutputStream stream, final Charset charset) {
        checkArgNotNull(stream, "stream");
        checkArgNotNull(charset, "charset");
        final StringReader reader = new StringReader(text != null ? text : "");
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, charset));
        copyAll(reader, writer);
    }
    
    public static void writeAllChars(final char[] chars, final String filename) {
        checkArgNotNull(filename, "filename");
        writeAllChars(chars, new File(filename));
    }

    public static void writeAllChars(final char[] chars, final String filename, final Charset charset) {
        checkArgNotNull(filename, "filename");
        checkArgNotNull(charset, "charset");
        writeAllChars(chars, new File(filename), charset);
    }

    public static void writeAllChars(final char[] chars, final File file) {
        checkArgNotNull(file, "file");
        writeAllChars(chars, file, Charset.forName("UTF8"));
    }

    public static void writeAllChars(
        final char[] chars, final File file, final Charset charset) {
        checkArgNotNull(file, "file");
        checkArgNotNull(charset, "charset");
        try {
            ensureParentDir(file);
            writeAllChars(chars, new FileOutputStream(file), charset);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeAllChars(final char[] chars, final OutputStream stream) {
        checkArgNotNull(stream, "stream");
        writeAllChars(chars, stream, Charset.forName("UTF8"));
    }

    public static void writeAllChars(final char[] chars, final OutputStream stream, final Charset charset) {
        checkArgNotNull(stream, "stream");
        checkArgNotNull(charset, "charset");
        final CharArrayReader reader = new CharArrayReader(chars != null ? chars : new char[0]);
        final BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, charset));
        copyAll(reader, writer);
    }

    public static void writeAllBytes(final byte[] data, final String filename) {
        checkArgNotNull(data, "data");
        checkArgNotNull(filename, "filename");
        writeAllBytes(data, new File(filename));
    }

    public static void writeAllBytes(final byte[] data, final File file) {
        checkArgNotNull(data, "data");
        checkArgNotNull(file, "file");
        try {
            ensureParentDir(file);
            writeAllBytes(data, new FileOutputStream(file));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeAllBytes(final byte[] data, final OutputStream stream) {
        checkArgNotNull(data, "data");
        checkArgNotNull(stream, "stream");
        final ByteArrayInputStream in = new ByteArrayInputStream(data);
        final BufferedOutputStream out = new BufferedOutputStream(stream);
        copyAll(in, out);
    }

    public static void copyAll(final Reader reader, final Writer writer) {
        checkArgNotNull(reader, "reader");
        checkArgNotNull(writer, "writer");
        try {
            final char[] data = new char[4096]; // copy in chunks of 4K
            int count;
            while ((count = reader.read(data)) >= 0) writer.write(data, 0, count);

            reader.close();
            writer.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyAll(final InputStream in, final OutputStream out) {
        checkArgNotNull(in, "in");
        checkArgNotNull(out, "out");
        try {
            final byte[] data = new byte[4096]; // copy in chunks of 4K
            int count;
            while ((count = in.read(data)) >= 0) {
                out.write(data, 0, count);
            }

            in.close();
            out.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ensureParentDir(final String filename) {
        ensureParentDir(new File(filename));
    }

    public static void ensureParentDir(final File file) {
        final File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            try {
                forceMkdir(parentDir);
            } catch (IOException e) {
                throw new RuntimeException(String.format("Could not create directory %s", parentDir), e);
            }
        }
    }

    public static void forceMkdir(final File directory) throws IOException {
        if (directory.exists()) {
            if (directory.isFile()) {
                throw new IOException(
                        "File '" + directory + "' exists and is not a directory. Unable to create directory.");
            }
        } else {
            if (!directory.mkdirs()) {
                throw new IOException("Unable to create directory " + directory);
            }
        }
    }

}
