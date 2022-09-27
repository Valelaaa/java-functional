package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.endava.internship.domain.Privilege.UPDATE;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.summarizingDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .map(User::getFirstName)
                .sorted(reverseOrder())
                .collect(toList());

    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted(comparingInt(User::getAge).reversed()
                        .thenComparing(User::getFirstName))
                .collect(toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream())
                .distinct().collect(toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream()
                .filter(user -> user.getAge() > age && user.getPrivileges()
                        .contains(UPDATE)).findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .map(user -> Pair.of(user.getPrivileges().size(), user))
                .collect(groupingBy(Pair::getKey, mapping(Pair::getValue, toList())));
    }

    @Override
    public Optional<Double> getAverageAgeForUsers(final List<User> users) {
        final DoubleSummaryStatistics doubleSummaryStatistics = users.stream()
                .collect(summarizingDouble(User::getAge));
        return (doubleSummaryStatistics.getCount() == 0) ? Optional.empty() : Optional.of(doubleSummaryStatistics.getAverage());
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return getNumberOfLastNames(users)
                .entrySet()
                .stream()
                .collect(groupingBy(Map.Entry::getValue))
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(list -> list.size() < 2)
                .map(list -> list.get(0).getKey());
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Stream.of(predicates)
                        .reduce(predicate -> true, Predicate::and))
                .collect(toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream()
                .map(mapFun)
                .collect(joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream())
                .distinct()
                .collect(
                        toMap(
                                Function.identity(),
                                privilege -> users.stream().filter(user ->
                                                user.getPrivileges().contains(privilege))
                                        .collect(toList())
                        )
                );
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .map(User::getLastName)
                .collect(groupingBy(Function.identity(), counting()));
    }
}
