package fatecipi.progweb.mymanga.specifications;

import fatecipi.progweb.mymanga.dto.manga.MangaSearchFilter;
import fatecipi.progweb.mymanga.models.Manga;
import fatecipi.progweb.mymanga.models.enums.Genres;
import fatecipi.progweb.mymanga.models.enums.MangaStatus;
import org.springframework.data.jpa.domain.Specification;

public class MangaSpec {
    private static boolean isInvalid(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static Specification<Manga> filterByTitle(String title) {
        return (root, query, builder) -> {
            if (isInvalid(title)) {
                return null;
            }
            return builder.like(builder.lower(root.get("title")), "%" + title.trim().toLowerCase() + "%");
        };
    }

    private static Specification<Manga> filterByAuthor(String author) {
        return (root, query, builder) -> {
            if (isInvalid(author)) {
                return null;
            }
            return builder.like(builder.lower(root.get("author")), "%" + author.trim().toLowerCase() + "%");
        };
    }

    private static Specification<Manga> filterByRatingGreatherThan(Double rating) {
        return (root, query, builder) -> {
            if (rating == null) {
                return null;
            }
            return builder.greaterThanOrEqualTo(root.get("rating"), rating);
        };
    }

    private static Specification<Manga> filterByKeywords(String keywords) {
        return (root, query, builder) -> {
            if (isInvalid(keywords)) {
                return null;
            }

            String term = "%" + keywords.trim().toLowerCase() + "%";
            return builder.or(
                    builder.like(builder.lower(root.get("keywords")), term),
                    builder.like(builder.lower(root.get("title")), term),
                    builder.like(builder.lower(root.get("description")), term)
            );
        };
    }

    private static Specification<Manga> filterByStatus(MangaStatus status) {
        return (root, query, builder) -> {
            if (status == null) {
                return null;
            }

            return builder.equal(root.get("status"), status);
        };
    }

    private static Specification<Manga> filterByGenres(Genres genres) {
        return (root, query, builder) -> {
            if (genres == null) {
                return null;
            }

            return builder.equal(root.get("genres"), genres);
        };
    }

    public static Specification<Manga> filter(MangaSearchFilter filter) {
        if (filter == null) {
            return Specification.unrestricted();
        }

        return Specification.<Manga>
                unrestricted()
                .and(filterByTitle(filter.title()))
                .and(filterByAuthor(filter.author()))
                .and(filterByRatingGreatherThan(filter.rating()))
                .and(filterByKeywords(filter.keywords()))
                .and(filterByStatus(filter.status()))
                .and(filterByGenres(filter.genres()));
    }
}

