/* Copyright 2009 The Stajistics Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stajistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author The Stajistics Project
 */
public abstract class StatsKeyMatcher {

    public static Builder build() {
        return new Builder();
    }

    public static StatsKeyMatcher all() {
        return AllMatcher.INSTANCE;
    }

    public static StatsKeyMatcher none() {
        return NoneMatcher.INSTANCE;
    }

    public static StatsKeyMatcher not(final StatsKeyMatcher delegate) {
        return new NegationMatcher(delegate);
    }
    
    public static StatsKeyMatcher prefix(final String prefix) {
        return new PrefixMatcher(prefix);
    }

    public static StatsKeyMatcher suffix(final String suffix) {
        return new SuffixMatcher(suffix);
    }

    public static StatsKeyMatcher contains(final String string) {
        return new ContainsMatcher(string);
    }

    public static StatsKeyMatcher depth(final int depth) {
        return new DepthMatcher(depth);
    }

    public Collection<StatsKey> filter(final Collection<StatsKey> keys) {
        List<StatsKey> filteredList = new ArrayList<StatsKey>(keys.size());
        for (StatsKey key : keys) {
            if (matches(key)) {
                filteredList.add(key);
            }
        }
        return Collections.unmodifiableCollection(filteredList);
    }

    public <T> Map<StatsKey,T> filter(final Map<StatsKey,T> map) {
        Map<StatsKey,T> filteredMap = new HashMap<StatsKey,T>(map.size());
        for (Map.Entry<StatsKey,T> entry : map.entrySet()) {
            if (matches(entry.getKey())) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(filteredMap);
    }

    public abstract boolean matches(StatsKey key);

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    /* NESTED CLASSES */
    
    public static class Builder {

        private final List<StatsKeyMatcher> matchers = new ArrayList<StatsKeyMatcher>(4);
        private boolean negateNext = false;

        protected Builder() {}

        private void addMatcher(StatsKeyMatcher matcher) {
            if (negateNext) {
                negateNext = false;
                matcher = StatsKeyMatcher.not(matcher);
            }

            matchers.add(matcher);
        }

        public Builder not() {
            negateNext = !negateNext;
            return this;
        }

        public Builder withPrefix(final String prefix) {
            addMatcher(prefix(prefix));
            return this;
        }

        public Builder withSuffix(final String suffix) {
            addMatcher(suffix(suffix));
            return this;
        }

        public Builder containing(final String string) {
            addMatcher(contains(string));
            return this;
        }

        public Builder atDepth(final int depth) {
            matchers.add(depth(depth));
            return this;
        }

        public StatsKeyMatcher matcher() {

            if (matchers.isEmpty()) {
                return NoneMatcher.INSTANCE;
            }

            if (matchers.size() == 1) {
                return matchers.get(0);
            }

            return new CompositeMatcher(matchers);
        }
    }

    private static class NegationMatcher extends StatsKeyMatcher {

        private final StatsKeyMatcher delegate;

        NegationMatcher(final StatsKeyMatcher delegate) {
            if (delegate == null) {
                throw new NullPointerException("delegate");
            }

            this.delegate = delegate;
        }

        @Override
        public boolean matches(final StatsKey key) {
            return !delegate.matches(key);
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof NegationMatcher)) {
                return false;
            }

            return delegate.equals(((NegationMatcher)other).delegate);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ delegate.hashCode();
        }
    }

    private static class CompositeMatcher extends StatsKeyMatcher {

        private final List<StatsKeyMatcher> matchers;

        CompositeMatcher(final List<StatsKeyMatcher> matchers) {
            if (matchers == null) {
                throw new NullPointerException("matchers");
            }
            if (matchers.isEmpty()) {
                throw new IllegalArgumentException("empty matchers");
            }

            this.matchers = matchers;
        }

        @Override
        public boolean matches(final StatsKey key) {
            int size = matchers.size();
            for (int i = 0; i < size; i++) {
                if (!matchers.get(i).matches(key)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof CompositeMatcher)) {
                return false;
            }

            return matchers.equals(((CompositeMatcher)other).matchers);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ matchers.hashCode();
        }        
    }

    private static class AllMatcher extends StatsKeyMatcher {

        private static final StatsKeyMatcher INSTANCE = new AllMatcher();

        @Override
        public boolean matches(final StatsKey key) {
            return true;
        }

        @Override
        public boolean equals(final Object other) {
            return other == INSTANCE;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    private static class NoneMatcher extends StatsKeyMatcher {

        private static final StatsKeyMatcher INSTANCE = new NoneMatcher();

        @Override
        public boolean matches(final StatsKey other) {
            return false;
        }

        @Override
        public boolean equals(final Object other) {
            return other == INSTANCE;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    private static class PrefixMatcher extends StatsKeyMatcher {

        private final String prefix;

        PrefixMatcher(final String prefix) {
            if (prefix == null) {
                throw new NullPointerException("prefix");
            }

            this.prefix = prefix;
        }

        @Override
        public boolean matches(final StatsKey key) {
            return key.getName().startsWith(prefix);
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof PrefixMatcher)) {
                return false;
            }

            return prefix.equals(((PrefixMatcher)other).prefix);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ prefix.hashCode();
        }
    }

    private static class SuffixMatcher extends StatsKeyMatcher {

        private final String suffix;

        SuffixMatcher(final String suffix) {
            if (suffix == null) {
                throw new NullPointerException("suffix");
            }

            this.suffix = suffix;
        }

        @Override
        public boolean matches(final StatsKey key) {
            return key.getName().endsWith(suffix);
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof SuffixMatcher)) {
                return false;
            }

            return suffix.equals(((SuffixMatcher)other).suffix);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ suffix.hashCode();
        }
    }

    private static class ContainsMatcher extends StatsKeyMatcher {

        private final String string;

        ContainsMatcher(final String string) {
            if (string == null) {
                throw new NullPointerException("string");
            }

            this.string = string;
        }

        @Override
        public boolean matches(final StatsKey key) {
            return key.getName().indexOf(string) > -1;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof ContainsMatcher)) {
                return false;
            }

            return string.equals(((ContainsMatcher)other).string);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ string.hashCode();
        }
    }

    private static class DepthMatcher extends StatsKeyMatcher {

        private final int depth;

        DepthMatcher(int depth) {
            if (depth < 1) {
                depth = 1;
            }
            this.depth = depth;
        }

        @Override
        public boolean matches(final StatsKey key) {
            int count = countHeirarchyDelimiters(key.getName()) - 1;
            return depth == count;
        }

        private int countHeirarchyDelimiters(final String name) {
            int count = 0;
            final char[] chars = name.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == StatsConstants.KEY_HIERARCHY_DELIMITER) {
                    count++;
                }
            }

            return count;
        }

        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof DepthMatcher)) {
                return false;
            }

            return depth == ((DepthMatcher)other).depth;
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ depth;
        }
    }
}
