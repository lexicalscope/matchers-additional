package com.lexicalscope;

import org.hamcrest.Description;

public class CompositeTransform<T2, T, S> implements Transform<T2, S> {
   private final Transform<T2, T> subsequentTransform;
   private final Transform<T, S> transform;

   public CompositeTransform(final Transform<T2, T> subsequentTransform, final Transform<T, S> transform) {
      this.subsequentTransform = subsequentTransform;
      this.transform = transform;
   }

   @Override public void describeTo(final Description description) {
      description.appendDescriptionOf(transform).appendText(" and then ").appendDescriptionOf(subsequentTransform);
   }

   @Override public T2 transform(final S item, final Description mismatchDescription) {
      final T intermediate = transform.transform(item, mismatchDescription);
      mismatchDescription.appendDescriptionOf(transform).appendText(" and then ");
      return subsequentTransform.transform(intermediate, mismatchDescription);
   }
}
