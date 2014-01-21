package com.lexicalscope;

import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;

public interface Transform<T, S> extends SelfDescribing {
   T transform(S item, Description mismatchDescription);
}