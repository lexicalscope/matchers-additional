package com.lexicalscope;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

public class MatchersJ8 {
    @SafeVarargs public static <E> org.hamcrest.Matcher<java.lang.Iterable<? extends E>> containsMatching(final org.hamcrest.Matcher<? super E>... itemMatchers) {
        return Matchers.contains(itemMatchers);
    }

    @SafeVarargs public static <E> org.hamcrest.Matcher<java.lang.Iterable<? extends E>> containsMatching(final Class<E> type, final org.hamcrest.Matcher<? super E>... itemMatchers) {
        return Matchers.contains(itemMatchers);
    }

    public static <F, P> Matcher<P> featureMatcher(
            final String featureName,
            final Function<? super P, ? extends F> extractor,
            final Matcher<? super F> submatcher) {
        return new TypeSafeMatcher<P>() {
            @Override public void describeTo(final Description description) {
                description.appendText(featureName).appendText(" -> ").appendDescriptionOf(submatcher);
            }

            @Override protected void describeMismatchSafely(final P item, final Description mismatchDescription) {
                mismatchDescription.appendText(featureName).appendText(" -> ");
                submatcher.describeMismatch(extractor.apply(item), mismatchDescription);
            }

            @Override protected boolean matchesSafely(final P item) {
                return submatcher.matches(extractor.apply(item));
            }
        };
    }

    public static <P> Matcher<P> matcher(
            final Consumer<Description> describeTo,
            final BiConsumer<P, Description> describeMismatch,
            final Predicate<P> matches) {
        return new TypeSafeMatcher<P>() {
            @Override public void describeTo(final Description description) {
                describeTo.accept(description);
            }

            @Override protected void describeMismatchSafely(final P item, final Description mismatchDescription) {
                describeMismatch.accept(item, mismatchDescription);
            }

            @Override protected boolean matchesSafely(final P item) {
                return matches.test(item);
            }
        };
    }

}
