package com.lexicalscope;

import static com.google.common.collect.ObjectArrays.concat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class MatchersAdditional {
   public static final class CollectionMatcherBuilder<T,S> {
      private final List<org.hamcrest.Matcher<? super T>> matchers = new ArrayList<>();
      private final Transform<T, S> transform;
      private boolean allowUnmatchedItems;

      public CollectionMatcherBuilder(final Transform<T,S> transform) {
         this.transform = transform;
         this.allowUnmatchedItems = true;
      }

      public static <E> CollectionMatcherBuilder<E, E> collectionMatcher() {
         return new CollectionMatcherBuilder<>(new NullTransform<E>());
      }

      public CollectionMatcherBuilder<T, S> only() {
         allowUnmatchedItems = false;
         return this;
      }

      @SafeVarargs public final CollectionMatcherBuilder<T,S> has(final org.hamcrest.Matcher<? super T>... itemMatchers) {
         for (final Matcher<? super T> matcher : itemMatchers) {
            matchers.add(matcher);
         }
         return this;
      }

      public final CollectionMatcherBuilder<T,S> has(final int count, final org.hamcrest.Matcher<? super T> itemMatcher) {
         for (int i = 0; i < count; i++) {
            matchers.add(itemMatcher);
         }
         return this;
      }

      public Matcher<Iterable<S>> inAnyOrder() {
         final List<org.hamcrest.Matcher<? super T>> theMatchers = Collections.unmodifiableList(new ArrayList<>(matchers));
         return new TypeSafeDiagnosingMatcher<Iterable<S>>() {
            @Override public void describeTo(final Description description) {
               description.appendText("iterable containing: ");
               final String separator = System.lineSeparator();
               for (final Matcher<? super T> matcher : theMatchers) {
                  description.appendText(separator).appendText("\t").appendDescriptionOf(matcher);
               }
            }

            @Override protected boolean matchesSafely(
                  final Iterable<S> items,
                  final Description mismatchDescription) {
               mismatchDescription.appendText("iterable containing: ");
               final String separator = System.lineSeparator();

               final List<T> itemsT = new LinkedList<>();
               for (final S item : items) {
                  mismatchDescription.appendText(separator).appendText("\t");
                  itemsT.add(transform.transform(item, mismatchDescription));
               }

               final List<org.hamcrest.Matcher<? super T>> consumeableMatchers = new LinkedList<>(matchers);

               for (final Iterator<T> itemsIterator = itemsT.iterator(); itemsIterator.hasNext();) {
                  final T item = itemsIterator.next();
                  for (final Iterator<Matcher<? super T>> matcherIterator = consumeableMatchers.iterator(); matcherIterator.hasNext();) {
                     final Matcher<? super T> matcher = matcherIterator.next();
                     if(matcher.matches(item)) {
                        matcherIterator.remove();
                        itemsIterator.remove();
                        break;
                     }
                  }
               }

               if(consumeableMatchers.isEmpty() && (allowUnmatchedItems || itemsT.isEmpty())) {
                  return true;
               }

               if(!consumeableMatchers.isEmpty()) {
                  mismatchDescription.appendText(separator);
                  mismatchDescription.appendText("matchers unmatched: ");
                  for (final Matcher<? super T> matcher : consumeableMatchers) {
                     mismatchDescription.appendText(separator).appendText("\t").appendDescriptionOf(matcher);
                  }
               }
               if(!allowUnmatchedItems && !itemsT.isEmpty()) {
                  mismatchDescription.appendText(separator);
                  mismatchDescription.appendText("items unmatched: ");
                  for (final T item : itemsT) {
                     mismatchDescription.appendText(separator).appendText("\t").appendValue(item);
                  }
               }
               return false;
            }
         };
      }

      public Matcher<Iterable<S>> inOrder() {
         final List<org.hamcrest.Matcher<? super T>> theMatchers = Collections.unmodifiableList(new ArrayList<>(matchers));
         return new TypeSafeDiagnosingMatcher<Iterable<S>>() {
            @Override public void describeTo(final Description description) {
               description.appendText("iterable containing in order: ");
               final String separator = System.lineSeparator();
               for (final Matcher<? super T> matcher : theMatchers) {
                  description.appendText(separator).appendText("\t").appendDescriptionOf(matcher);
               }
            }

            @Override protected boolean matchesSafely(
                  final Iterable<S> items,
                  final Description mismatchDescription) {
               mismatchDescription.appendText("iterable containing in order: ");
               final String separator = System.lineSeparator();

               final List<T> itemsT = new LinkedList<>();
               for (final S item : items) {
                  mismatchDescription.appendText(separator).appendText("\t");
                  itemsT.add(transform.transform(item, mismatchDescription));
               }

               {
                  final Iterator<T> itemsIterator = itemsT.iterator();
                  final Iterator<Matcher<? super T>> matcherIterator = matchers.iterator();

                  while(itemsIterator.hasNext() && matcherIterator.hasNext()) {
                     final Matcher<? super T> matcher = matcherIterator.next();
                     final T item = itemsIterator.next();
                     if(!matcher.matches(item)) {
                        mismatchDescription.appendText(separator).appendText("expected ").appendDescriptionOf(matcher).appendText("but found ");
                        matcher.describeMismatch(item, mismatchDescription);
                        return false;
                     }
                  }

                  if(matcherIterator.hasNext() || itemsIterator.hasNext() && !allowUnmatchedItems) {
                     if(matcherIterator.hasNext()) {
                        mismatchDescription.appendText(separator);
                        mismatchDescription.appendText("matchers unmatched: ");
                        while (matcherIterator.hasNext()) {
                           final Matcher<? super T> matcher = matcherIterator.next();
                           mismatchDescription.appendText(separator).appendText("\t").appendDescriptionOf(matcher);
                        }
                     }
                     if(itemsIterator.hasNext() && !allowUnmatchedItems) {
                        mismatchDescription.appendText(separator);
                        mismatchDescription.appendText("items unmatched: ");
                        while (matcherIterator.hasNext()) {
                           final T item = itemsIterator.next();
                           mismatchDescription.appendText(separator).appendText("\t").appendValue(item);
                        }
                     }
                     return false;
                  }
                  return true;
               }
            }
         };
      }
   }

   public static final class TransformMatcherBuilder<T, S> {
      private final Transform<T, S> transform;

      public TransformMatcherBuilder(final Transform<T, S> transform) {
         this.transform = transform;
      }

      public org.hamcrest.Matcher<S> matches(final org.hamcrest.Matcher<T> matcher) {
         return adaptMatcherWithTransform(transform, matcher);
      }

      @SafeVarargs public final CollectionMatcherBuilder<T, S> has(final org.hamcrest.Matcher<? super T>... itemMatchers) {
         return new CollectionMatcherBuilder<T, S>(transform).has(itemMatchers);
      }

      public CollectionMatcherBuilder<T,S> has(final int count, final org.hamcrest.Matcher<? super T> itemMatcher) {
         return new CollectionMatcherBuilder<T,S>(transform).has(count, itemMatcher);
      }

      public <T2> CollectionMatcherBuilder<T2,S> then(final Transform<T2, T> subsequentTransform) {
         return new CollectionMatcherBuilder<T2,S>(new CompositeTransform<T2,T,S>(subsequentTransform, transform));
      }
   }

   @SafeVarargs public static <E> CollectionMatcherBuilder<E,E> has(final org.hamcrest.Matcher<? super E>... itemMatchers) {
      return CollectionMatcherBuilder.<E>collectionMatcher().has(itemMatchers);
   }

   public static <E> CollectionMatcherBuilder<E,E> has(final int count, final org.hamcrest.Matcher<? super E> itemMatcher) {
      return CollectionMatcherBuilder.<E>collectionMatcher().has(count, itemMatcher);
   }

   @SafeVarargs public static <E> org.hamcrest.Matcher<java.lang.Iterable<? extends E>> containsMatching(final org.hamcrest.Matcher<? super E>... itemMatchers) {
      return Matchers.contains(itemMatchers);
   }

   public static <T, S> org.hamcrest.Matcher<S> adaptMatcherWithTransform(
         final Transform<T, S> transform,
         final org.hamcrest.Matcher<T> matcher) {
      return new TypeSafeDiagnosingMatcher<S>() {
         @Override public void describeTo(final Description description) {
            description
            .appendText("when transformed by ")
            .appendDescriptionOf(transform)
            .appendText(" then ")
            .appendDescriptionOf(matcher);
         }

         @Override protected boolean matchesSafely(final S item, final Description mismatchDescription) {
            mismatchDescription.appendText("when transformed by ");
            final T itemT = transform.transform(item, mismatchDescription);

            mismatchDescription.appendText(" then ");
            matcher.describeMismatch(itemT, mismatchDescription);

            return matcher.matches(itemT);
         }
      };
   }

   public static <T, S> TransformMatcherBuilder<T, S> after(final Transform<T, S> transform) {
      return new TransformMatcherBuilder<>(transform);
   }

   @SafeVarargs public static <T> Matcher<T> allOf(final Matcher<? super T> matcher, final Matcher<? super T> ... matchers)
   {
      return Matchers.allOf(concat(matcher, matchers));
   }

   @SafeVarargs public static <T> Matcher<T> anyOf(final Matcher<? super T> matcher, final Matcher<? super T> ... matchers)
   {
      return Matchers.anyOf(concat(matcher, matchers));
   }

   public static <T> T with(final T var) {
      return var;
   }
}
