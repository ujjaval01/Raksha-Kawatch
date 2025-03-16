import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.ui.rakshakawatch.R

class FragmentTools : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tools, container, false)

        // Find LinearLayouts by their IDs
        val ll1 = view.findViewById<LinearLayout>(R.id.ll1)
        val ll2 = view.findViewById<LinearLayout>(R.id.ll2)


        // Set Click Listeners for each tool
        ll1.setOnClickListener { showToast("Share Location clicked") }
        ll2.setOnClickListener { showToast("Emergency Contact clicked") }


        return view
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}
