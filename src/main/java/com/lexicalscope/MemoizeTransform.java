package com.lexicalscope;

import org.hamcrest.Description;
import org.hamcrest.StringDescription;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class MemoizeTransform<T, S> implements Transform<T, S> {
   private static final class TransfromResult<T> {
      private final T itemT;
      private final String mismatchDescription;

      public TransfromResult(final T itemT, final StringDescription mismatchDescription) {
         this.itemT = itemT;
         this.mismatchDescription = mismatchDescription.toString();
      }
   }

   private final LoadingCache<S, TransfromResult<T>> memoized;
   private final Transform<T, S> delegate;

   public MemoizeTransform(final Transform<T, S> delegate) {
      this.delegate = delegate;
      memoized = CacheBuilder.newBuilder()
            .weakKeys()
            .build(
                  new CacheLoader<S, TransfromResult<T>>() {
                     @Override
                     public TransfromResult<T> load(final S item) {
                        final StringDescription stringDescription = new StringDescription();
                        return new TransfromResult<T>(delegate.transform(item, stringDescription), stringDescription);
                     }
                  });
   }

   @Override public void describeTo(final Description description) {
      delegate.describeTo(description);
   }

   @Override public T transform(final S item, final Description mismatchDescription) {
      final TransfromResult<T> transfromResult = memoized.getUnchecked(item);
      mismatchDescription.appendText(transfromResult.mismatchDescription);
      return transfromResult.itemT;
   }
}
