/*
 * Copyright 2024 INVIRGANCE LLC

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.invirgance.convirgance;

/**
 * Represents a runtime exception specific to the Convirgance API, providing
 * focused and contained error messages for debugging.
 *
 * @author jbanes
 */
public class ConvirganceException extends RuntimeException
{

    public ConvirganceException()
    {
        super();
    }

    /**
     * Constructs a new exception with the specified message.
     *
     * @param message The error message explaining what went wrong.
     */
    public ConvirganceException(String message)
    {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause The underlying exception that caused this error.
     */
    public ConvirganceException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified message and cause.
     *
     * @param message The error message explaining what went wrong.
     * @param cause The underlying exception that caused this error.
     */
    public ConvirganceException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
