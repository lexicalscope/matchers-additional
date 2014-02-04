package com.lexicalscope;

import org.hamcrest.Description;

public class NullTransform<T> implements Transform<T, T> {
   @Override public void describeTo(final Description description) {
   }

   @Override public T transform(final T item, final Description mismatchDescription) {
      mismatchDescription.appendValue(item);
      return item;
   }
}
