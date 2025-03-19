
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.ui.rakshakawatch.R
import com.ui.rakshakawatch.fragments.MapFragment

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Find LinearLayouts by their IDs
        val mapLocation = view.findViewById<LinearLayout>(R.id.mapLocation)

        // Set Click Listener for Map Location
        mapLocation.setOnClickListener {
            // Create an instance of your MapFragment
            val mapFragment = MapFragment()

            // Replace the current fragment with MapFragment
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, mapFragment)
            transaction.addToBackStack(null) // Allows going back to the previous fragment
            transaction.commit()
        }

        return view
    }
}
