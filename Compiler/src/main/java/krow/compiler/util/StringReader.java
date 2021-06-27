package krow.compiler.util;


import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StringReader implements Iterable<Character> {
    public final char[] chars;
    protected transient int position;
    
    public StringReader(String string) {
        this.chars = string.toCharArray();
    }
    
    public StringReader(char[] chars) {
        this.chars = chars;
    }
    
    public String readRest() {
        StringBuilder builder;
        for (builder = new StringBuilder(); this.canRead(); ++this.position) {
            builder.append(this.chars[this.position]);
        }
        
        return builder.toString();
    }
    
    public String read(int length) {
        int end = this.position + length;
        
        StringBuilder builder;
        for (builder = new StringBuilder(); this.position < end && this.position < this.chars.length; ++this.position) {
            builder.append(this.chars[this.position]);
        }
        
        return builder.toString();
    }
    
    public String readUntil(char c) {
        StringBuilder builder;
        for (builder = new StringBuilder(); this.canRead(); ++this.position) {
            char test = this.chars[this.position];
            if (c == test) {
                break;
            }
            
            builder.append(test);
        }
        
        return builder.toString();
    }
    
    public String readUntilEscape(char c) {
        StringBuilder builder = new StringBuilder();
        
        for (boolean ignore = false; this.canRead(); ++this.position) {
            char test = this.chars[this.position];
            if (ignore) {
                ignore = false;
            } else if (test == '\\') {
                ignore = true;
            } else if (c == test) {
                break;
            }
            
            builder.append(test);
        }
        
        return builder.toString();
    }
    
    public String readUntilMatches(Function<String, Boolean> function) {
        StringBuilder builder = new StringBuilder();
        
        while (this.canRead()) {
            char test = this.chars[this.position];
            builder.append(test);
            ++this.position;
            if (function.apply(builder.toString())) {
                break;
            }
        }
        
        return builder.toString();
    }
    
    public String readUntilMatches(Pattern pattern) {
        StringBuilder builder;
        for (builder = new StringBuilder(); this.canRead(); ++this.position) {
            char test = this.chars[this.position];
            builder.append(test);
            if (pattern.matcher(builder.toString()).matches()) {
                break;
            }
        }
        
        return builder.toString();
    }
    
    public String readUntilMatchesAfter(Pattern pattern, char end) {
        StringBuilder builder = new StringBuilder();
        
        for (boolean canEnd = false; this.canRead(); ++this.position) {
            char test = this.chars[this.position];
            if (test == end) {
                canEnd = true;
            }
            
            if (canEnd && pattern.matcher(builder.toString()).matches()) {
                break;
            }
            
            builder.append(test);
        }
        
        return builder.toString();
    }
    
    public boolean hasApproaching(int index) {
        return this.remaining().length > index;
    }
    
    public char getApproaching(int index) {
        return this.remaining()[index];
    }
    
    public boolean hasNext() {
        return this.position < this.chars.length - 1;
    }
    
    public void skip() {
        if (this.canRead()) {
            ++this.position;
        }
        
    }
    
    public void skip(int i) {
        this.position += i;
    }
    
    public void rotateBack(int i) {
        this.position -= i;
    }
    
    public boolean canRead() {
        return this.position < this.chars.length && this.position >= 0;
    }
    
    public int getPosition() {
        return this.position;
    }
    
    public void setPosition(int i) {
        this.position = i;
    }
    
    public int length() {
        return this.chars.length;
    }
    
    public char current() {
        if (this.canRead()) {
            return this.chars[this.position];
        } else {
            throw new RuntimeException("Limit exceeded!");
        }
    }
    
    public char previous() {
        if (this.position - 1 >= 0) {
            return this.chars[this.position - 1];
        } else {
            throw new RuntimeException("Limit exceeded!");
        }
    }
    
    public char next() {
        if (this.position + 1 < this.chars.length) {
            return this.chars[this.position + 1];
        } else {
            throw new RuntimeException("Limit exceeded!");
        }
    }
    
    public char rotate() {
        if (this.canRead()) {
            char c = this.chars[this.position];
            ++this.position;
            return c;
        } else {
            throw new RuntimeException("Limit exceeded!");
        }
    }
    
    public void reset() {
        this.position = 0;
    }
    
    public String toString() {
        return new String(this.chars);
    }
    
    public int charCount(char c) {
        int i = 0;
        char[] var3 = this.chars;
        int var4 = var3.length;
        
        for (int var5 = 0; var5 < var4; ++var5) {
            char ch = var3[var5];
            if (ch == c) {
                ++i;
            }
        }
        
        return i;
    }
    
    public char[] remaining() {
        return Arrays.copyOfRange(this.chars, this.position, this.chars.length);
    }
    
    @NotNull
    public Iterator<Character> iterator() {
        return new StringReader.Iterative();
    }
    
    public StringReader clone() {
        StringReader reader = new StringReader(this.chars);
        reader.position = this.position;
        return reader;
    }
    
    protected class Iterative implements Iterator<Character> {
        int cursor;
        int lastRet = -1;
        int size;
        
        Iterative() {
            this.size = StringReader.this.chars.length;
        }
        
        public boolean hasNext() {
            return this.cursor != StringReader.this.chars.length;
        }
        
        public Character next() {
            this.checkForComodification();
            int i = this.cursor;
            if (i >= StringReader.this.chars.length) {
                throw new NoSuchElementException();
            } else {
                this.cursor = i + 1;
                return StringReader.this.chars[this.lastRet = i];
            }
        }
        
        public void remove() {
            throw new ConcurrentModificationException();
        }
        
        public void forEachRemaining(Consumer<? super Character> consumer) {
            Objects.requireNonNull(consumer);
            int size = StringReader.this.chars.length;
            int i = this.cursor;
            if (i < size) {
                while (i != size) {
                    consumer.accept(StringReader.this.chars[i++]);
                }
                
                this.cursor = i;
                this.lastRet = i - 1;
                this.checkForComodification();
            }
        }
        
        final void checkForComodification() {
        }
    }
}
