package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users.stream()
                .map(User::getFirstName)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users.stream()
                .sorted(Comparator.comparingInt(User::getAge).reversed()
                        .thenComparing(User::getFirstName))
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users.stream()
                .flatMap(user -> user.getPrivileges().stream())
                .distinct().
                collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users.stream()
                .filter(user -> user.getAge() > age && user.getPrivileges()
                        .contains(Privilege.UPDATE)).findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users.stream()
                .map(user -> Pair.of(user.getPrivileges().size(),user))
                .collect(groupingBy(Pair::getKey,Collectors
                        .mapping(Pair::getValue, Collectors.toList())));
    }
    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        DoubleSummaryStatistics doubleSummaryStatistics = users.stream()
                .collect(Collectors.summarizingDouble(User::getAge));
        return (doubleSummaryStatistics.getCount() == 0) ? -1 :doubleSummaryStatistics.getAverage();
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return users.stream()
                .map(User::getLastName)
                .collect(groupingBy(Function.identity(),Collectors.counting()))
                .entrySet().stream()
                .collect(groupingBy(Map.Entry::getValue))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                .findFirst()
                .map(Map.Entry::getValue)
                .filter(list -> list.size() < 2)
                .map(list -> list.get(0).getKey());
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users.stream()
                .filter(Stream.of(predicates)
                        .reduce(predicate->true,Predicate::and))
                .collect(Collectors.toList());
    }
    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users.stream()
                .map(mapFun).collect(Collectors.joining(delimiter));
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
                                           .collect(Collectors.toList())
                           )
                   );
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users.stream()
                .map(User::getLastName)
                .collect(groupingBy(Function.identity(),Collectors.counting()));
    }
}
