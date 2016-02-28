package victoriaslmn.bookcrossing.data.common

import com.google.gson.annotations.SerializedName
import java.util.*

object Fields {
    const val PHOTO_200 = "photo_200";
    const val PHOTO_100 = "photo_100";
}

class VkResponse<T> ()  {
    @SerializedName("response")
    var response : T? = null

    @SerializedName("error")
    var error: VkError? = null
}

class PagingResponse<T>() {
    @SerializedName("count")
    var count: Int = 0

    @SerializedName("items")
    var items: List<T> = ArrayList()
}

class VkError {
    object Type{
        const val NONAME = 1 //Произошла неизвестная ошибка. Попробуйте повторить запрос позже.
        const val APP_TURN_OFF = 2 //Приложение выключено. Необходимо включить приложение в настройках https://vk.com/editapp?id={Ваш API_ID} или использовать тестовый режим (test_mode=1)
        const val METHOD_NOT_EXIST = 3 //Передан неизвестный метод. Проверьте, правильно ли указано название вызываемого метода: http://vk.com/dev/methods.
        const val NOT_CORRECT_SING = 4 //Неверная подпись. Проверьте правильность формирования подписи запроса: https://vk.com/dev/api_nohttps
        const val USER_AUTH_ERROR = 5 //Авторизация пользователя не удалась.Убедитесь, что Вы используете верную схему авторизации. Для работы с методами без префикса secure Вам нужно авторизовать пользователя одним из этих способов: http://vk.com/dev/auth_sites, http://vk.com/dev/auth_mobile.
        const val SO_MANY_REQUESTS = 6 //Слишком много запросов в секунду. Задайте больший интервал между вызовами или используйте метод execute. Подробнее об ограничениях на частоту вызовов см. на странице http://vk.com/dev/api_requests.
        const val DO_NOT_HAVE_PERMISSIONS = 7 //Нет прав для выполнения этого действия. Проверьте, получены ли нужные права доступа при авторизации. Это можно сделать с помощью метода account.getAppPermissions.
        const val NOT_CORRECT_REQUEST = 8 //Неверный запрос. Проверьте синтаксис запроса и список используемых параметров (его можно найти на странице с описанием метода).
        const val SO_MANY_SAME_DO = 9 //Слишком много однотипных действий. Нужно сократить число однотипных обращений. Для более эффективной работы Вы можете использовать execute или JSONP.
        const val SERVER_INNER_EXCEPTION = 10 //Произошла внутренняя ошибка сервера. Попробуйте повторить запрос позже.
        const val TEST_MODE_OR_NEED_AUTH = 11 //В тестовом режиме приложение должно быть выключено или пользователь должен быть залогинен. Выключите приложение в настройках https://vk.com/editapp?id={Ваш API_ID}
        const val INTER_CAPTCHA = 14 //Требуется ввод кода с картинки (Captcha). Процесс обработки этой ошибки подробно описан на отдельной странице.
        const val ACCESS_DENIED = 15 //Доступ запрещён. Убедитесь, что Вы используете верные идентификаторы, и доступ к контенту для текущего пользователя есть в полной версии сайта.
        const val HTTPS_NEED = 16 //Требуется выполнение запросов по протоколу HTTPS, т.к. пользователь включил настройку, требующую работу через безопасное соединение.Чтобы избежать появления такой ошибки, в Standalone-приложении Вы можете предварительно проверять состояние этой настройки у пользователя методом account.getInfo.
        const val VALID_USER_NEED = 17 //Требуется валидация пользователя. Действие требует подтверждения — необходимо перенаправить пользователя на служебную страницу для валидации.
        const val FORBIDDEN_FOR_NOT_STANDALONE = 20 //Данное действие запрещено для не Standalone приложений. Если ошибка возникает несмотря на то, что Ваше приложение имеет тип Standalone, убедитесь, что при авторизации Вы используете redirect_uri=https://oauth.vk.com/blank.html. Подробнее см. http://vk.com/dev/auth_mobile.
        const val FORBIDDEN_FOR_NOT_STANDALONE_NOT_OPEN_API = 21 //Данное действие разрешено только для Standalone и Open API приложений.
        const val METHOD_TURN_OFF = 23 //Метод был выключен. Все актуальные методы ВК API, которые доступны в настоящий момент, перечислены здесь: http://vk.com/dev/methods.
        const val NEED_USER_CONFIRMATION = 24 //Требуется подтверждение со стороны пользователя.
        const val NOT_CORRECT_QUERY = 100 //Один из необходимых параметров был не передан или неверен. Проверьте список требуемых параметров и их формат на странице с описанием метода.
        const val NOT_CORRECT_API_ID_APP = 101 //Неверный API ID приложения. Найдите приложение в списке администрируемых на странице http://vk.com/apps?act=settings и укажите в запросе верный API_ID (идентификатор приложения).
        const val NOT_CORRECT_USER_ID = 113 //Неверный идентификатор пользователя. Убедитесь, что Вы используете верный идентификатор. Получить ID по короткому имени можно методом utils.resolveScreenName.
        const val NOT_CORRECT_TIMESTAMP = 150 //Неверный timestamp. Получить актуальное значение Вы можете методом utils.getServerTime.
        const val ALBUM_ACCESS_DENIED = 200 //Доступ к альбому запрещён. Убедитесь, что Вы используете верные идентификаторы (для пользователей owner_id положительный, для сообществ — отрицательный), и доступ к запрашиваемому контенту для текущего пользователя есть в полной версии сайта.
        const val AUDIO_ACCESS_DENIED = 201 //Доступ к аудио запрещён. Убедитесь, что Вы используете верные идентификаторы (для пользователей owner_id положительный, для сообществ — отрицательный), и доступ к запрашиваемому контенту для текущего пользователя есть в полной версии сайта.
        const val GROUP_ACCESS_DENIED = 203 //Доступ к группе запрещён. Убедитесь, что текущий пользователь является участником или руководителем сообщества (для закрытых и частных групп и встреч).
        const val ALBUM_FULL = 300 //Альбом переполнен. Перед продолжением работы нужно удалить лишние объекты из альбома или использовать другой альбом.
        const val ACTION_FORBIDDEN = 500 //Действие запрещено. Вы должны включить переводы голосов в настройках приложения. Проверьте настройки приложения: http://vk.com/editapp?id={Ваш API_ID}&section=payments
        const val AD_NOT_HAVE_PERMISSION = 600 //Нет прав на выполнение данных операций с рекламным кабинетом.
        const val AD_ERROR = 603 //Произошла ошибка при работе с рекламным кабинетом.
        const val ID_DOCUMENT_NOT_CORRECT = 1150 //Неверный идентификатор документа
        const val NOT_HAVE_DOCUMENT_NAME = 1152 //Не передано название документа
        const val NOT_ACCESS_TO_DOCUMENT = 1153 //Нет доступа к документу.
        const val CAN_NOT_SAVE_FILE = 105 //Невозможно сохранить файл.
    }

    @SerializedName("error_code")
    var errorCode:Int? = null

    @SerializedName("error_msg")
    var errorMsg:String? = null
}