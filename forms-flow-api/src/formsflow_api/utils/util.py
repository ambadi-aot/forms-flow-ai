"""Common utils.

CORS pre-flight decorator. A simple decorator to add the options
method to a Request Class.
camel_to_snake - Converts camel case to snake case.
validate_sort_order_and_order_by - Utility function to validate
if sort order and sort order by is correct.
translate - Translate the response to provided language
"""
import re

from .constants import ALLOW_ALL_ORIGINS
from .enums import ApplicationSortingParameters
from .translations.translations import translations


def cors_preflight(methods: str = "GET"):
    """Render an option method on the class."""

    def wrapper(f):  # pylint: disable=invalid-name
        def options(self, *args, **kwargs):  # pylint: disable=unused-argument
            return (
                {"Allow": "GET"},
                200,
                {
                    "Access-Control-Allow-Origin": ALLOW_ALL_ORIGINS,
                    "Access-Control-Allow-Methods": methods,
                    "Access-Control-Allow-Headers": "Authorization, Content-Type",
                },
            )

        setattr(f, "options", options)
        return f

    return wrapper


def camel_to_snake(name: str) -> str:
    """Convert camel case to snake case."""
    s_1 = re.sub("(.)([A-Z][a-z]+)", r"\1_\2", name)
    return re.sub("([a-z0-9])([A-Z])", r"\1_\2", s_1).lower()


def validate_sort_order_and_order_by(order_by: str, sort_order: str) -> bool:
    """Validate sort order and order by."""
    if order_by not in [
        ApplicationSortingParameters.Id,
        ApplicationSortingParameters.Name,
        ApplicationSortingParameters.Status,
        ApplicationSortingParameters.Modified,
        ApplicationSortingParameters.FormName,
    ]:
        order_by = None
    else:
        if order_by == ApplicationSortingParameters.Name:
            order_by = ApplicationSortingParameters.FormName
        order_by = camel_to_snake(order_by)
    if sort_order not in ["asc", "desc"]:
        sort_order = None
    return order_by, sort_order


def translate(to_lang: str, data: dict) -> dict:
    """Translate the response to provided language.

    will return the translated object if there is match
    else return the original object
    """
    try:
        translated_data = {}
        if to_lang not in translations:
            raise KeyError
        for key, value in data.items():
            # if matching translation is present for either key / value,
            # then translated string is used
            # original string otherwise
            translated_data[
                translations[to_lang][key] if key in translations[to_lang] else key
            ] = (
                translations[to_lang][value]
                if value in translations[to_lang]
                else value
            )
        return translated_data
    except KeyError as err:
        raise err
    except Exception as err:
        raise err
