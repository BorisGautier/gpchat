package cm.tbg.gpchat.activities.authentication

sealed class ViewState {
    data class Verify(val phoneNumber: String) : ViewState()


}